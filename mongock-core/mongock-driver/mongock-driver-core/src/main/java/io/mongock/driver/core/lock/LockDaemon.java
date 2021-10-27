package io.mongock.driver.core.lock;

import io.mongock.driver.api.lock.LockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class LockDaemon extends Thread {

  private static final Logger logger = LoggerFactory.getLogger(LockDaemon.class);
  private final LockManager lockManager;
  private volatile boolean cancelled = false;
  private volatile boolean active = false;

  public LockDaemon(LockManager lockManager) {
    setName("mongock-lock-daemon");
    this.lockManager = lockManager;
    setDaemon(true);
  }

  @Override
  public void run() {
    logger.info("...starting mongock lock daemon");
    while(!cancelled) {
      try {
        if(active) {
          logger.debug("Mongock lock daemon ensuring lock");
          lockManager.ensureLockDefault();
        } else {
          logger.debug("Mongock lock daemon in loop but not ensuring lock because it's been activated yet");
        }
      } catch(Exception ex) {
        logger.error("Error ensuring the lock at the lock daemon", ex);
        cancelled = true;
        break;
      }
      repose(lockManager.getMillisUntilRefreshRequired());
    }
  }

  private void repose(long timeForResting) {
    try {
      logger.debug("...mongock lock daemon going to sleep: " + timeForResting + "ms");
      sleep(timeForResting);
    } catch (InterruptedException ex) {
      logger.warn("Interrupted exception ignored");
    }
  }

  public void cancel() {
    logger.info("...cancelling mongock lock daemon");
    cancelled = true;
  }

  public boolean isCancelled() {
    return cancelled;
  }

  public void activate() {
    active = true;
  }

}
