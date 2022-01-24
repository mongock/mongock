package io.mongock.driver.api.lock;

import java.io.Closeable;


public interface LockManager extends Closeable {

  /**
   * @return lock's key
   */
  default String getDefaultKey() {
    return "DEFAULT_LOCK";
  }

  /**
   * <p>Tries to acquire the default lock regardless who is the current owner.</p>
   * <p>If the lock is already acquired by the current LockChecker or is expired, will be updated</p>
   * <p>In case the lock is acquired by another LockChecker, it will wait until the current lock is expired
   * and will try to acquire it again. This will be repeated as many times as (maxTries - 1)</p>
   *
   * @throws LockCheckException if the lock cannot be acquired
   */
  void acquireLockDefault() throws LockCheckException;


  /**
   * <p>Tries to refresh the default lock when the current LockChecker has the lock or , when the lock
   * is expired, is the last owner</p>
   * <p>Notice that it does not try to acquire when is acquired by another LockChecker</p>
   *
   * @throws LockCheckException if, in case of needed, the lock cannot be refreshed
   */
  void ensureLockDefault() throws LockCheckException;

  /**
   * <p>Eventually release the default lock when is acquired by the current LockChecker.</p>
   * <p>When the lock is not acquired by the current LockChecker, it won't make any change.
   * Does not throw any exception neither.</p>
   * <p>Idempotent operation.</p>
   * <p>"Eventually" means that this operation is delegated to the daemon</p>
   */
  void releaseLockDefaultEventually();

  /**
   * @return lock try frequency
   */
  long getLockTryFrequency();

  /**
   * @return Lock's owner
   */
  String getOwner();

  /**
   * @return if lock is held
   */
  boolean isLockHeld();


  long getMillisUntilRefreshRequired();

  @Override
  void close();
}
