package com.github.cloudyrock.mongock;

/**
 * <p>Repository class to manage lock in database</p>
 *
 * @since 04/04/2018
 */
public interface LockRepository extends Repository{

  /**
   * If there is a lock in the database with the same key, updates it if either is expired or both share the same owner.
   * If there is no lock with the same key, it's inserted.
   *
   * @param newLock lock to replace the existing one or be inserted.
   * @throws LockPersistenceException if there is a lock in database with same key, but is expired and belong to
   *                                  another owner or cannot insert/update the lock for any other reason
   */
  void insertUpdate(LockEntry newLock) throws LockPersistenceException;

  /**
   * If there is a lock in the database with the same key and owner, updates it.Otherwise throws a LockPersistenceException
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
