package io.mongock.runner.core.executor.operation.change;

import com.google.gson.Gson;
import io.mongock.api.config.TransactionStrategy;
import io.mongock.api.config.executor.ChangeExecutorConfiguration;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.common.SystemChange;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.driver.api.entry.ChangeState;
import io.mongock.driver.api.entry.ChangeType;
import io.mongock.driver.api.entry.ExecutedChangeEntry;
import io.mongock.driver.api.lock.LockManager;
import io.mongock.runner.core.executor.Executor;
import io.mongock.runner.core.executor.changelog.ChangeLogRuntime;
import io.mongock.runner.core.internal.BeforeChangeSetItem;
import io.mongock.runner.core.internal.ChangeLogItem;
import io.mongock.runner.core.internal.ChangeSetItem;
import io.mongock.utils.Triple;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static io.mongock.driver.api.entry.ChangeState.EXECUTED;
import static io.mongock.driver.api.entry.ChangeState.FAILED;
import static io.mongock.driver.api.entry.ChangeState.IGNORED;
import static io.mongock.driver.api.entry.ChangeState.ROLLBACK_FAILED;
import static io.mongock.driver.api.entry.ChangeState.ROLLED_BACK;
import static io.mongock.driver.api.entry.ChangeType.BEFORE_EXECUTION;
import static io.mongock.driver.api.entry.ChangeType.EXECUTION;

@NotThreadSafe
public abstract class MigrationExecutorBase<CONFIG extends ChangeExecutorConfiguration> implements Executor {

  private static final Logger logger = LoggerFactory.getLogger(MigrationExecutorBase.class);

  protected final Boolean globalTransactionEnabled;
  protected final Deque<Triple<Object, ChangeSetItem, Exception>> changeSetsToRollBack = new ArrayDeque<>();
  protected final ConnectionDriver driver;
  protected final String serviceIdentifier;
  protected final boolean trackIgnored;
  protected final Set<ChangeLogItem<ChangeSetItem>> changeLogs;
  protected final Map<String, Object> metadata;
  private final ChangeLogRuntime changeLogRuntime;
  protected boolean executionInProgress = false;
  protected final String executionId;
  private final TransactionStrategy transactionStrategy;
  protected List<ExecutedChangeEntry> executedChangeEntries = null;


  public MigrationExecutorBase(String executionId,
                               Set<ChangeLogItem<ChangeSetItem>> changeLogs,
                               ConnectionDriver driver,
                               ChangeLogRuntime changeLogRuntime,
                               CONFIG config) {
    this.executionId = executionId;
    this.driver = driver;
    this.changeLogRuntime = changeLogRuntime;
    this.metadata = config.getMetadata();
    this.serviceIdentifier = config.getServiceIdentifier();
    this.trackIgnored = config.isTrackIgnored();
    this.changeLogs = changeLogs;
    this.globalTransactionEnabled = config.getTransactionEnabled().orElse(null);
    this.transactionStrategy = config.getTransactionStrategy();
  }

  @Override
  public boolean isExecutionInProgress() {
    return this.executionInProgress;
  }

  @Override
  public Boolean executeMigration() {
    initializationAndValidation();
    try (LockManager lockManager = driver.getLockManager()) {
      // load executed changeEntries to check if any of the changeSets need to be executed
      loadExecutedChangeEntries();
      if (!this.isThereAnyChangeSetItemToBeExecuted(changeLogs)) {
        logger.info("Mongock skipping the data migration. All change set items are already executed or there is no change set item.");
        return false;
      }
      lockManager.acquireLockDefault();
      // when lock is acquired, it's needed to reload executed changeEntries to get last changes
      loadExecutedChangeEntries();
      String executionHostname = generateExecutionHostname(executionId);
      logger.info("Mongock starting the data migration sequence id[{}]...", executionId);
      processMigration(changeLogs, executionId, executionHostname);
      return true;
    } finally {
      this.executionInProgress = false;
      logger.info("Mongock has finished");
    }
  }

  protected void processMigration(Collection<ChangeLogItem<ChangeSetItem>> changeLogs, String executionId, String executionHostname) {
    prepareForStageExecutionIfApply(isStrategyPerMigration());
    driver.getTransactioner()
        .filter(t -> isStrategyPerMigration() && isDriverTransactional())
        .orElse(Runnable::run)
        .executeInTransaction(() -> processChangeLogs(executionId, executionHostname, changeLogs));
  }

  protected void processChangeLogs(String executionId, String executionHostname, Collection<ChangeLogItem<ChangeSetItem>> changeLogs) {
    for (ChangeLogItem<ChangeSetItem> changeLog : changeLogs) {
      processSingleChangeLog(executionId, executionHostname, changeLog);
    }
  }

  protected void processSingleChangeLog(String executionId, String executionHostname, ChangeLogItem<ChangeSetItem> changeLog) {
    try {
      // if strategy == changeLog only needs to store the processed changeSets per changeLog
      prepareForStageExecutionIfApply(isStrategyPerChangeUnit() && changeLog.isTransactional());
      Object changeLogInstance = getChangeLogInstance(changeLog.getType());
      loopRawChangeSets(executionId, executionHostname, changeLogInstance, changeLog, changeLog.getBeforeItems());
      processChangeLogInTransactionIfApplies(executionId, executionHostname, changeLogInstance, changeLog);
      // todo add a test to ensure the following condition
      // if strategy == changeLog , regardless if the changeLog is transactional, the queue for the changeSets
      // to rollback need to be cleared
      if(changeLog.getType().isAnnotationPresent(SystemChange.class) && changeLog.getType().getAnnotation(SystemChange.class).updatesSystemTable()) {
        loadExecutedChangeEntries();
      }
      clearChangeSetsToRollbackIfApply(isStrategyPerChangeUnit());
    } catch (Exception e) {
      if (changeLog.isFailFast()) {
        rollbackProcessedChangeSetsIfApply(executionId, executionHostname, changeSetsToRollBack);
        throw e;
      }
    }
  }

  protected Object getChangeLogInstance(Class<?> changeLogClass) {
    injectDependenciesFromDriver();
    return changeLogRuntime.getInstance(changeLogClass);
  }

  protected void processChangeLogInTransactionIfApplies(String executionId, String executionHostname, Object changeLogInstance, ChangeLogItem<ChangeSetItem> changeLog) {
    driver.getTransactioner()
        .filter(c -> isDriverTransactional() && isStrategyPerChangeUnit() && changeLog.isTransactional())
        .orElse(Runnable::run)
        .executeInTransaction(() -> loopRawChangeSets(executionId, executionHostname, changeLogInstance, changeLog, changeLog.getChangeSetItems()));
  }

  protected void loopRawChangeSets(String executionId, String executionHostName, Object changeLogInstance, ChangeLogItem<ChangeSetItem> changeLog, List<? extends ChangeSetItem> changeSets) {
    for (ChangeSetItem changeSet : changeSets) {
      //if driver is no transactional or, being the strategy per ChangeUnit, the changeSet is non-transactional(before or changeLog flagged as non-transactional)
      //the changeSet needs to be queued to be rolled back, in case a change fails
      if (!isDriverTransactional() || (isStrategyPerChangeUnit() && (changeSet instanceof BeforeChangeSetItem || !changeLog.isTransactional()))) {
        changeSetsToRollBack.push(new Triple<>(changeLogInstance, changeSet, null));
      }
      processSingleChangeSet(executionId, executionHostName, changeLogInstance, changeSet);
    }
  }


  /**
   * changeSetsToRollBack collection contains "all and only" the changeSets to rollback "manually" in case of any
   * exception occurs, sorted in reverse order.
   * <p>
   * scenarios for the changeSetsToRollBack collection
   * - strategy == MIGRATION and transactional: It should be empty, as all the changeSets, before included, should be
   * rollBacked in the transaction.
   * - strategy == MIGRATION and non-transactional: It should contain all the executed changeSets in the entire
   * migration, main and before, in execution reverse order
   * - strategy == CHANGE_LOG and transactional: It should contain only the before methods executed for the current
   * changeLog
   * - strategy == CHANGE_LOG and non-transactional: It should contain all the executed changeSets in the current
   * changeLog, main and before, in execution reverse order
   */
  protected void rollbackProcessedChangeSetsIfApply(String executionId, String hostname, Deque<Triple<Object, ChangeSetItem, Exception>> processedChangeSets) {
    logger.info("Mongock migration aborted and DB transaction not enabled. Starting manual rollback process");
    processedChangeSets.forEach(triple -> {
      try {
        rollbackIfPresentAndTrackChangeEntry(executionId, hostname, triple.getFirst(), triple.getSecond(), triple.getThird());
      } catch (Exception e) {
        throw e instanceof MongockException ? (MongockException) e : new MongockException(e);
      }
    });

  }

  protected void processSingleChangeSet(String executionId, String executionHostname, Object changeLogInstance, ChangeSetItem changeSet) {
    try {
      executeAndLogChangeSet(executionId, executionHostname, changeLogInstance, changeSet);
    } catch (Exception e) {
      processExceptionOnChangeSetExecution(e, changeSet, changeSet.isFailFast());
    }
  }

  protected String generateExecutionHostname(String executionId) {
    String hostname;
    try {
      hostname = InetAddress.getLocalHost().getHostName();
    } catch (Exception e) {
      hostname = "unknown-host." + executionId;
    }

    if (StringUtils.isNotEmpty(serviceIdentifier)) {
      hostname += "-";
      hostname += serviceIdentifier;
    }
    return hostname;
  }

  protected boolean isThereAnyChangeSetItemToBeExecuted(Collection<ChangeLogItem<ChangeSetItem>> changeLogs) {
    return changeLogs.stream()
        .map(ChangeLogItem::getAllChangeItems)
        .flatMap(List::stream)
        .anyMatch(changeSetItem -> changeSetItem.isRunAlways() || !this.isAlreadyExecuted(changeSetItem));
  }

  protected boolean isAlreadyExecuted(ChangeSetItem changeSetItem) {
    return this.executedChangeEntries.stream().anyMatch(changeEntry -> changeEntry.getChangeId().equals(changeSetItem.getId()) && changeEntry.getAuthor().equals(changeSetItem.getAuthor()));
  }

  protected void executeAndLogChangeSet(String executionId, String executionHostname, Object changelogInstance, ChangeSetItem changeSetItem) throws IllegalAccessException, InvocationTargetException {
    ChangeEntry changeEntry = null;
    boolean alreadyExecuted = false;
    ChangeType type = changeSetItem.isBeforeChangeSets() ? BEFORE_EXECUTION : EXECUTION;
    try {
      if (!(alreadyExecuted = isAlreadyExecuted(changeSetItem)) || changeSetItem.isRunAlways()) {
        logger.debug("executing changeSet[{}]", changeSetItem.getId());
        final long executionTimeMillis = executeChangeSetMethod(changeSetItem.getMethod(), changelogInstance);
        changeEntry = buildChangeEntry(executionId, executionHostname, changeSetItem, executionTimeMillis, EXECUTED, type);
        logger.debug("successfully executed changeSet[{}]", changeSetItem.getId());

      } else {
        changeEntry = buildChangeEntry(executionId, executionHostname, changeSetItem, -1L, IGNORED, type);

      }
    } catch (Exception ex) {
      logger.debug("failure when executing changeSet[{}]", changeSetItem.getId());
      changeEntry = buildChangeEntry(executionId, executionHostname, changeSetItem, -1L, FAILED, type, ex, null);
      throw ex;
    } finally {
      if (changeEntry != null) {
        logChangeEntry(changeEntry, changeSetItem, alreadyExecuted);
        trackChangeEntry(changeSetItem, changeEntry, alreadyExecuted);
      }
    }
  }

  private void trackChangeEntry(ChangeSetItem changeSetItem, ChangeEntry changeEntry, boolean alreadyExecuted) {
    // if not runAlways or, being runAlways, it hasn't been executed before
    if (!changeSetItem.isRunAlways() || !alreadyExecuted) {
      //if not ignored or, being ignored, should be tracked anyway
      if (changeEntry.getState() != IGNORED || trackIgnored) {
        driver.getChangeEntryService().saveOrUpdate(changeEntry);
      }
    }
  }

  protected void rollbackIfPresentAndTrackChangeEntry(String executionId,
                                                      String executionHostname,
                                                      Object changeLogInstance,
                                                      ChangeSetItem changeSetItem,
                                                      Exception changeSetException) throws InvocationTargetException, IllegalAccessException {

    if (changeSetItem.getRollbackMethod().isPresent()) {
      logger.debug("rolling back changeSet[{}]", changeSetItem.getId());
      Optional<Exception> rollbackExceptionOpt = Optional.empty();
      try {
        executeChangeSetMethod(changeSetItem.getRollbackMethod().get(), changeLogInstance);
        logger.debug("successfully rolled back changeSet[{}]", changeSetItem.getId());
      } catch (Exception ex) {
        logger.debug("failure when rolling back changeSet[{}]:\n{}", changeSetItem.getId(), ex.getMessage());
        rollbackExceptionOpt = Optional.of(ex);
        throw ex;
      } finally {
        ChangeType type = changeSetItem.isBeforeChangeSets() ? BEFORE_EXECUTION : EXECUTION;
        ChangeState state = rollbackExceptionOpt.map(ex -> ROLLBACK_FAILED).orElse(ROLLED_BACK);
        ChangeEntry changeEntry = buildChangeEntry(
            executionId,
            executionHostname,
            changeSetItem,
            -1L,
            state,
            type,
            changeSetException,
            rollbackExceptionOpt.orElse(null));
        logChangeEntry(changeEntry, changeSetItem, false);
        trackChangeEntry(changeSetItem, changeEntry, false);
      }
    } else {
      logger.warn("ChangeSet[{}] does not provide rollback method", changeSetItem.getId());
    }

  }

  private void logChangeEntry(ChangeEntry changeEntry, ChangeSetItem changeSetItem, boolean alreadyExecuted) {
    switch (changeEntry.getState()) {
      case EXECUTED:
        logger.info("{}APPLIED - {}", alreadyExecuted ? "RE-" : "", changeEntry.toPrettyString());
        break;
      case IGNORED:
        logger.info("PASSED OVER - {}", changeSetItem.toPrettyString());
        break;
      case FAILED:
        logger.info("FAILED OVER - {}", changeSetItem.toPrettyString());
        break;
      case ROLLED_BACK:
        logger.info("ROLLED BACK - {}", changeSetItem.toPrettyString());
        break;
      case ROLLBACK_FAILED:
        logger.info("ROLL BACK FAILED- {}", changeSetItem.toPrettyString());
        break;
    }
  }
  protected ChangeEntry buildChangeEntry(String executionId,
                                         String executionHostname,
                                         ChangeSetItem changeSetItem,
                                         long executionTimeMillis,
                                         ChangeState state,
                                         ChangeType type) {
    return buildChangeEntry(executionId, executionHostname, changeSetItem, executionTimeMillis, state, type, null, null);

  }

  protected ChangeEntry buildChangeEntry(String executionId,
                                         String executionHostname,
                                         ChangeSetItem changeSetItem,
                                         long executionTimeMillis,
                                         ChangeState state,
                                         ChangeType type,
                                         Exception executionException,
                                         Exception rollbackException) {
    if(executionException == null && rollbackException == null) {
      return ChangeEntry.instance(
          executionId,
          changeSetItem.getAuthor(),
          state,
          type,
          changeSetItem.getId(),
          changeSetItem.getMethod().getDeclaringClass().getName(),
          changeSetItem.getMethod().getName(),
          executionTimeMillis,
          executionHostname,
          metadata);
    }
    Map<String, String> errorMap = new HashMap<>();
    if (executionException != null) {
      errorMap.put("execution-error", io.mongock.utils.StringUtils.getStackTrace(executionException));
    }
    if (rollbackException != null) {
      errorMap.put("rollback-error", io.mongock.utils.StringUtils.getStackTrace(rollbackException));
    }
    return ChangeEntry.failedInstance(
        executionId,
        changeSetItem.getAuthor(),
        state,
        type,
        changeSetItem.getId(),
        changeSetItem.getMethod().getDeclaringClass().getName(),
        changeSetItem.getMethod().getName(),
        executionTimeMillis,
        executionHostname,
        metadata,
        new Gson().toJson(errorMap)
    );
  }


  protected long executeChangeSetMethod(Method changeSetMethod, Object changeLogInstance) throws IllegalAccessException, InvocationTargetException {
    final long startingTime = System.currentTimeMillis();
    changeLogRuntime.runChangeSet(changeLogInstance, changeSetMethod);
    return System.currentTimeMillis() - startingTime;
  }

  protected void processExceptionOnChangeSetExecution(Exception exception, ChangeSetItem changeSetItem, boolean throwException) {
    String exceptionMsg = exception instanceof InvocationTargetException
        ? ((InvocationTargetException) exception).getTargetException().getMessage()
        : exception.getMessage();
    Method method = changeSetItem.getMethod();
    String finalMessage = String.format("Error in method[%s.%s] : %s", method.getDeclaringClass().getSimpleName(), method.getName(), exceptionMsg);
    updateRollbackChangeSet(changeSetItem, exception);
    if (throwException) {
      throw new MongockException(finalMessage, exception);

    } else {
      logger.warn(finalMessage, exception);
    }
  }

  private void updateRollbackChangeSet(ChangeSetItem changeSetItem, Exception exception) {
    Iterator<Triple<Object, ChangeSetItem, Exception>> iterator = changeSetsToRollBack.iterator();
    boolean finished = false;
    while (iterator.hasNext() && !finished) {
      Triple<Object, ChangeSetItem, Exception> item = iterator.next();
      if (changeSetItem.getId().equals(item.getSecond().getId())) {
        item.setThird(exception);
        finished = true;
      }
    }
  }


  protected void initializationAndValidation() throws MongockException {
    this.executionInProgress = true;
    driver.initialize();
    driver.runValidation();
    changeLogRuntime.initialize(driver.getLockManager());
  }

  private void injectDependenciesFromDriver() {
    changeLogRuntime.updateDriverDependencies(driver.getDependencies());
  }

  protected void loadExecutedChangeEntries() {
    this.executedChangeEntries = this.driver.getChangeEntryService().getExecuted();
  }

  protected final boolean isDriverTransactional() {
    return globalTransactionEnabled == null ? driver.isTransactionable() : globalTransactionEnabled && driver.isTransactionable();
  }

  protected final boolean isStrategyPerChangeUnit() {
    return transactionStrategy == null || transactionStrategy == TransactionStrategy.CHANGE_UNIT;
  }

  protected final boolean isStrategyPerMigration() {
    return transactionStrategy == TransactionStrategy.EXECUTION;
  }

  protected void prepareForStageExecutionIfApply(boolean applyPreparation) {
    if (applyPreparation) {
      driver.prepareForExecutionBlock();
    }
  }

  protected void clearChangeSetsToRollbackIfApply(boolean applyPreparation) {
    if (applyPreparation) {
      changeSetsToRollBack.clear();
    }
  }


}
