package com.github.cloudyrock.mongock;

import com.mongodb.DB;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mongock runner
 *
 * @since 26/07/2014
 */
public class Mongock implements Closeable {
  private static final Logger logger = LoggerFactory.getLogger(Mongock.class);

  protected final ChangeEntryRepository changeEntryRepository;
  protected final ChangeService changeService;
  protected final LockChecker lockChecker;
  protected final Closeable mongoClientCloseable;

  private boolean throwExceptionIfCannotObtainLock;
  private boolean enabled;
  protected Map<String, Object> metadata;
  protected MongoDatabase changelogMongoDatabase;
  protected Map<Class, Object> dependencies = new HashMap<>();

  protected Mongock(
      ChangeEntryRepository changeEntryRepository,
      Closeable mongoClientCloseable,
      ChangeService changeService,
      LockChecker lockChecker) {
    this.changeEntryRepository = changeEntryRepository;
    this.mongoClientCloseable = mongoClientCloseable;
    this.changeService = changeService;
    this.lockChecker = lockChecker;
  }

  /**
   * @return true if an execution is in progress, in any process.
   */
  public boolean isExecutionInProgress() {
    return lockChecker.isLockHeld();
  }

  /**
   * @return true if Mongock runner is enabled and able to run, otherwise false
   */
  public boolean isEnabled() {
    return enabled;
  }

  void setMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
  }

  void setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
    this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
  }

  void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  void addChangeSetDependency(Object dependency) {
    this.dependencies.put(dependency.getClass(), dependency);
  }

  /**
   * This method just forces the type in cases where is needed to override
   * a dependency with a child class
   */
  <T> void addChangeSetDependency(Class<T> type, T dependency) {
    this.dependencies.put(type, dependency);
  }

  public void execute() {
    if (!isEnabled()) {
      logger.info("Mongock is disabled. Exiting.");
      return;
    }

    try {
      lockChecker.acquireLockDefault();
      executeMigration();
    } catch (LockCheckException lockEx) {

      if (throwExceptionIfCannotObtainLock) {
        logger.error(lockEx.getMessage());//only message as the exception is propagated
        throw new MongockException(lockEx.getMessage());
      } else {
        logger.warn(lockEx.getMessage());
        logger.warn("Mongock did not acquire process lock. EXITING WITHOUT RUNNING DATA MIGRATION");
      }

    } finally {
      lockChecker.releaseLockDefault();//we do it anyway, it's idempotent
      logger.info("Mongock has finished his job.");
    }

  }

  /**
   * Closes the Mongo instance used by Mongock.
   * This will close either the connection Mongock was initiated with or that which was internally created.
   */
  public void close() throws IOException {
    mongoClientCloseable.close();
  }

  private void executeMigration() {
    logger.info("Mongock starting the data migration sequence..");
    final String executionId = changeService.getNewExecutionId();
    for (Class<?> changelogClass : changeService.fetchChangeLogs()) {

      Object changelogInstance;
      try {
        changelogInstance = changeService.createInstance(changelogClass);
        List<Method> changeSetMethods = changeService.fetchChangeSets(changelogInstance.getClass());
        for (Method changeSetMethod : changeSetMethods) {
          executeIfNewOrRunAlways(changelogInstance, changeSetMethod, changeService.createChangeEntry(executionId, changeSetMethod, this.metadata));
        }

      } catch (NoSuchMethodException | IllegalAccessException | InstantiationException e) {
        throw new MongockException(e.getMessage(), e);
      } catch (InvocationTargetException e) {
        Throwable targetException = e.getTargetException();
        throw new MongockException(targetException.getMessage(), e);
      }

    }
  }

  private void executeIfNewOrRunAlways(Object changelogInstance, Method changeSetMethod, ChangeEntry changeEntry) throws IllegalAccessException, InvocationTargetException {
    try {
      if (changeEntryRepository.isNewChange(changeEntry)) {
        final long executionTimeMillis = executeChangeSetMethod(changeSetMethod, changelogInstance);
        changeEntry.setExecutionMillis(executionTimeMillis);
        changeEntryRepository.save(changeEntry);
        logger.info("APPLIED - {}", changeEntry);
      } else if (changeService.isRunAlwaysChangeSet(changeSetMethod)) {
        final long executionTimeMillis = executeChangeSetMethod(changeSetMethod, changelogInstance);
        changeEntry.setExecutionMillis(executionTimeMillis);
        changeEntryRepository.save(changeEntry);
        logger.info("RE-APPLIED - {}", changeEntry);
      } else {
        logger.info("PASSED OVER - {}", changeEntry);
      }
    } catch (MongockException e) {
      logger.error(e.getMessage(), e);
    }
  }

  protected Optional<Object> getDependency(Class type) {
    return this.dependencies.entrySet().stream()
        .filter(entrySet -> type.isAssignableFrom(entrySet.getKey()))
        .map(Map.Entry::getValue)
        .findFirst();
  }

  protected long executeChangeSetMethod(Method changeSetMethod, Object changeLogInstance)
      throws IllegalAccessException, InvocationTargetException {
    final long startingTime = System.currentTimeMillis();
    List<Object> changelogInvocationParameters = new ArrayList<>(changeSetMethod.getParameterTypes().length);
    for (Class<?> parameter : changeSetMethod.getParameterTypes()) {
      Optional<Object> parameterOptional = this.getDependency(parameter);
      if (parameterOptional.isPresent()) {
        changelogInvocationParameters.add(parameterOptional.get());
      } else {
        throw new MongockException(String.format("Method[%s] using argument[%s] not injected", changeSetMethod.getName(), parameter.getName()));
      }
    }
    logMethodWithArguments(changeSetMethod.getName(), changelogInvocationParameters);
    changeSetMethod.invoke(changeLogInstance, changelogInvocationParameters.toArray());
    return System.currentTimeMillis() - startingTime;
  }

  private void logMethodWithArguments(String methodName, List<Object> changelogInvocationParameters) {
    String arguments = changelogInvocationParameters.stream()
        .map(obj -> obj != null ? obj.getClass().getName() : "{null argument}")
        .collect(Collectors.joining(", "));
    logger.info("method[{}] with arguments: [{}]", methodName, arguments);

  }
}
