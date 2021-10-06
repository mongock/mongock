package io.mongock.runner.core.executor;

import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.lock.LockCheckException;
import io.mongock.runner.core.event.EventPublisher;
import io.mongock.runner.core.event.result.MigrationSuccessResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongockRunnerImpl implements MongockRunner {
  private static final Logger logger = LoggerFactory.getLogger(MongockRunnerImpl.class);

  private final Executor executor;
  private final boolean throwExceptionIfCannotObtainLock;
  private final EventPublisher eventPublisher;

  private boolean enabled;

  public MongockRunnerImpl(Executor executor,
                           boolean throwExceptionIfCannotObtainLock,
                           boolean enabled,
                           EventPublisher eventPublisher) {
    this.executor = executor;
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
    return executor.isExecutionInProgress();
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
        Object result = executor.executeMigration();
        eventPublisher.publishMigrationSuccessEvent(new MigrationSuccessResult(result));
      } catch (LockCheckException lockEx) {
        MongockException mongockException = new MongockException(lockEx);
        eventPublisher.publishMigrationFailedEvent(mongockException);
        if (throwExceptionIfCannotObtainLock) {
          logger.error("Mongock did not acquire process lock. EXITING WITHOUT RUNNING DATA MIGRATION", lockEx);
          throw mongockException;

        } else {
          logger.warn("Mongock did not acquire process lock. EXITING WITHOUT RUNNING DATA MIGRATION", lockEx);
        }

      } catch (Exception ex) {
        MongockException exWrapper = MongockException.class.isAssignableFrom(ex.getClass()) ? (MongockException) ex : new MongockException(ex);
        logger.error("Error in mongock process. ABORTED MIGRATION", exWrapper);
        eventPublisher.publishMigrationFailedEvent(exWrapper);
        throw exWrapper;

      }
    }
  }

}
