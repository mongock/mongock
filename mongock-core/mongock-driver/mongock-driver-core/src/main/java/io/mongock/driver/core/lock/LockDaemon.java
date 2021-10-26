package io.mongock.driver.core.lock;

import io.mongock.driver.api.lock.LockCheckException;
import io.mongock.driver.api.lock.LockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockDaemon extends Thread {

  private static final Logger logger = LoggerFactory.getLogger(LockDaemon.class);
  private final LockManager lockManager;
  private final long initialDelay;
  private volatile boolean cancelled = false;


  public LockDaemon(LockManager lockManager) {
    this(lockManager, -1);
  }
  public LockDaemon(LockManager lockManager, long initialDelay) {
    setName("mongock-lock-keeper-daemon");
    this.initialDelay = initialDelay;
    this.lockManager = lockManager;
    setDaemon(true);
  }

  @Override
  public void start() {
    logger.info("...starting mongock lock daemon");
    if(initialDelay > 0 ) {
      repose(initialDelay);
    }

    while(!cancelled) {
      try {
        lockManager.ensureLockDefault();
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
      sleep(timeForResting);
    } catch (InterruptedException ex) {
      logger.warn("Interrupted exception ignored");
    }
  }

  public void cancel() {
    logger.info("...cancelling mongock lock daemon");
    cancelled = true;
  }

}
