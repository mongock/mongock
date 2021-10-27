package io.mongock.driver.core.lock;

import io.mongock.driver.api.lock.LockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockDaemon extends Thread {

  private static final Logger logger = LoggerFactory.getLogger(LockDaemon.class);
  private final LockManager lockManager;
  private volatile boolean cancelled = false;
  private volatile boolean active = false;

  public LockDaemon(LockManager lockManager) {
    setName("mongock-lock-daemon-" + getId());
    this.lockManager = lockManager;
    setDaemon(true);
  }

  @Override
  public void run() {
    logger.info("Starting mongock lock daemon...");
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
        cancel();
        break;
      }
      repose(lockManager.getMillisUntilRefreshRequired());
    }
    logger.info("Cancelled mongock lock daemon");
  }

  private void repose(long timeForResting) {
    try {
      logger.debug("Mongock lock daemon going to sleep: " + timeForResting + "ms");
      sleep(timeForResting);
    } catch (InterruptedException ex) {
      logger.warn("Interrupted exception ignored");
    }
  }

  public void cancel() {
    logger.info("Cancelling mongock lock daemon...");
    cancelled = true;
  }

  public void activate() {
    active = true;
  }

}
