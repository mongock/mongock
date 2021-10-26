package io.mongock.driver.core.lock;

import io.mongock.driver.api.lock.LockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockDaemon extends Thread {

  private static final Logger logger = LoggerFactory.getLogger(LockDaemon.class);
  private final LockManager lockManager;
  private final long initialDelay;
  private volatile boolean cancelled = false;
  private volatile boolean started = false;


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
    if(initialDelay > 0 ) {
      repose(initialDelay);
    }
    while(!cancelled) {
      ensureLock();
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
    cancelled = true;
  }

  private void ensureLock() {
    lockManager.ensureLockDefault();
  }

}
