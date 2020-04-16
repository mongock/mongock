package com.github.cloudyrock.mongock;

/**
 * <p>Repository class to manage lock in database</p>
 *
 * @since 04/04/2018
 */
public interface LockRepository extends Repository{

  /**
   * If there is already a lock with newLock.key and owner == newLock.owner, it's updated with the newLock's values
   * If there is a lock that belong to another owner, but it's expired, it's replaced.
   * If there is no lock for newLock.key, newLock is inserted.
   * If there is an active lock(not expired) with key== newLock.key but belongs to another owner, it throws an
   * LockPersistenceException.
   *
   * @param newLock lock to replace the existing one or be inserted.
   * @throws LockPersistenceException if there is a lock in database with same key, but is expired and belong to
   *                                  another owner or cannot insert/update the lock for any other reason
   */
  void insertUpdate(LockEntry newLock) throws LockPersistenceException;

  /**
   * If there is a lock in the database with the same key and owner, updates it.
   * Otherwise throws a LockPersistenceException. This means that will throws a LockPersistenceException
   * even if there is no Lock whatsoever.
   *
   * @param newLock lock to replace the existing one.
   * @throws LockPersistenceException if there is no lock in the database with the same key and owner or cannot update
   *                                  the lock for any other reason
   */
  void updateIfSameOwner(LockEntry newLock) throws LockPersistenceException;

  /**
   * Retrieves a lock by key
   *
   * @param lockKey key
   * @return LockEntry
   */
  LockEntry findByKey(String lockKey);

  /**
   * Removes from database all the locks with the same key(only can be one) and owner
   *
   * @param lockKey lock key
   * @param owner   lock owner
   */
  void removeByKeyAndOwner(String lockKey, String owner);

}
