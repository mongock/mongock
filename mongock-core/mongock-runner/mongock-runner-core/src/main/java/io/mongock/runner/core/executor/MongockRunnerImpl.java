package io.mongock.runner.core.executor;

import io.mongock.api.exception.MongockException;
import io.mongock.api.exception.MongockRollbackException;
import io.mongock.driver.api.lock.LockCheckException;
import io.mongock.runner.core.event.EventPublisher;
import io.mongock.runner.core.event.result.MigrationSuccessResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongockRunnerImpl implements MongockRunner {
  private static final Logger logger = LoggerFactory.getLogger(MongockRunnerImpl.class);

  private final Executor systemUpdateExecutor;
  private final Executor operationExecutor;
  private final boolean throwExceptionIfCannotObtainLock;
  private final EventPublisher eventPublisher;

  private boolean enabled;

  public MongockRunnerImpl(Executor systemUpdateExecutor,
                           Executor operationExecutor,
                           boolean throwExceptionIfCannotObtainLock,
                           boolean enabled,
                           EventPublisher eventPublisher) {
    this.systemUpdateExecutor = systemUpdateExecutor;
    this.operationExecutor = operationExecutor;
    this.enabled = enabled;
    this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
    this.eventPublisher = eventPublisher;
  }

  public void forceEnable() {
    this.enabled = true;
  }

  /**
   * @return true if an execution is in progress, in any process.
   */
  public boolean isExecutionInProgress() {
    return systemUpdateExecutor.isExecutionInProgress() || operationExecutor.isExecutionInProgress();
  }

  /**
   * @return true if Mongock runner is enabled and able to run, otherwise false
   */
  public boolean isEnabled() {
    return enabled;
  }

  public void execute() throws MongockException {
    if (!isEnabled()) {
      logger.info("Mongock is disabled. Exiting.");
    } else {      
      try {
        eventPublisher.publishMigrationStarted();
        systemUpdateExecutor.executeMigration();
        Object result = operationExecutor.executeMigration();
        eventPublisher.publishMigrationSuccessEvent(new MigrationSuccessResult(result));
      } catch (LockCheckException lockEx) {
        MongockException mongockException = new MongockException(lockEx);
        eventPublisher.publishMigrationFailedEvent(mongockException);
        if (throwExceptionIfCannotObtainLock) {
          logger.error("Mongock did not acquire process lock. EXITING WITHOUT RUNNING OPERATION", lockEx);
          throw mongockException;

        } else {
          logger.warn("Mongock did not acquire process lock. EXITING WITHOUT RUNNING OPERATION", lockEx);
        }

      } catch (Exception ex) {
        if (MongockRollbackException.class.isAssignableFrom(ex.getClass())) {
          MongockRollbackException mongockRollbackException = (MongockRollbackException)ex;
          logger.error("Error in mongock process. ABORTED OPERATION");
          logger.error("EXECUTION error detail:", mongockRollbackException.getExecutionException());
          logger.error("ROLLBACK error detail:", mongockRollbackException.getRollbackException());
          eventPublisher.publishMigrationFailedEvent(mongockRollbackException.getRollbackException());
          throw mongockRollbackException.getRollbackException();
        }
        else {
          MongockException exWrapper = MongockException.class.isAssignableFrom(ex.getClass()) ? (MongockException) ex : new MongockException(ex);
          logger.error("Error in mongock process. ABORTED OPERATION", exWrapper);
          eventPublisher.publishMigrationFailedEvent(exWrapper);
          throw exWrapper;
        }
      }
    }
  }

}
