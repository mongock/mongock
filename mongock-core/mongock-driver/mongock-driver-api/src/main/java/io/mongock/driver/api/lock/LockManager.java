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
   * <p>Release the default lock when is acquired by the current LockChecker.</p>
   * <p>When the lock is not acquired by the current LockChecker, it won't make any change.
   * Does not throw any exception neither.</p>
   * <p>Idempotent operation.</p>
   */
  void releaseLockDefault();

  /**
   * <p>If the flag 'waitForLog' is set, indicates the maximum time it will wait for the lock in total.</p>
   *
   * @param millis max waiting time for lock. Must be greater than 0
   * @return LockChecker object for fluent interface
   */
  LockManager setLockQuitTryingAfterMillis(long millis);

  /**
   * @return lock try frequency
   */
  long getLockTryFrequency();

  /**
   * <p>Updates the maximum number of tries to acquire the lock, if the flag 'waitForLog' is set </p>
   * <p>Default 1</p>
   *
   * @param millis number of tries
   * @return LockChecker object for fluent interface
   */
  LockManager setLockTryFrequencyMillis(long millis);

  /**
   * <p>Indicates the number of milliseconds the lock will be acquired for</p>
   * <p>Default 3 minutes</p>
   *
   * @param lockAcquiredForMillis milliseconds the lock will be acquired for
   * @return LockChecker object for fluent interface
   */
  LockManager setLockAcquiredForMillis(long lockAcquiredForMillis);

  /**
   * @return Lock's owner
   */
  String getOwner();

  /**
   * @return if lock is held
   */
  boolean isLockHeld();

  /**
   * force to delete all the locks in the database. Mainly for test environment. Not recommended production use
   */
  void clean();


  @Override
  void close();
}
