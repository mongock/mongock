package io.mongock.runner.core.executor.operation.migrate;

import io.mongock.runner.core.executor.ChangeExecutorBase;
import io.mongock.api.config.executor.ChangeExecutorConfiguration;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.api.lock.LockManager;
import io.mongock.runner.core.executor.changelog.ChangeLogRuntime;
import io.mongock.runner.core.executor.changelog.ChangeLogServiceBase;
import io.mongock.runner.core.internal.ChangeLogItem;
import java.lang.reflect.AnnotatedElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Collection;
import java.util.function.Function;

@NotThreadSafe
public abstract class MigrateExecutorBase extends ChangeExecutorBase<ChangeExecutorConfiguration> {

  private static final Logger logger = LoggerFactory.getLogger(MigrateExecutorBase.class);


  public MigrateExecutorBase(String executionId,
                               ChangeLogServiceBase changeLogService,
                               ConnectionDriver driver,
                               ChangeLogRuntime changeLogRuntime,
                               Function<AnnotatedElement, Boolean> annotationFilter,
                               ChangeExecutorConfiguration config) {
    super(executionId,
          changeLogService,
          driver,
          changeLogRuntime,
          annotationFilter,
          config.getMetadata(),
          config.getServiceIdentifier(),
          config.isTrackIgnored(),
          config.getTransactionEnabled(),
          config.getTransactionStrategy(),
          config);
  }

  @Override
  public Boolean executeMigration() {
    initializationAndValidation();
    // prepare changeLogs to filter which ones require to be rolled back
    Collection<ChangeLogItem> changeLogs = this.fetchAndPrepareChangeLogs();
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
  
  protected Collection<ChangeLogItem> fetchAndPrepareChangeLogs() {
    // By default returns all, the executor will process all of them.
    return this.changeLogService.fetchChangeLogs();
  }
   
  @Override
  protected void validateChangeLog(ChangeLogItem changeLog) {
    if (changeLog.isSystem()) {
      logger.warn("Invalid ChangeUnit[{}]. System changeUnits are only allowed for internal purposes. It will be treated as not system change.",
              changeLog.getId());
      changeLog.setSystem(false);
    }
  }
}
