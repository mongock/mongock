package com.github.cloudyrock.mongock;

import com.mongodb.DuplicateKeyException;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Date;

/**
 * <p>Repository class to manage lock in database</p>
 *
 *
 * @since 04/04/2018
 */
class LockMongoRepository extends MongoRepositoryBase implements LockRepository {

  LockMongoRepository(String collectionName, MongoDatabase db) {
    super(db, collectionName, new String[]{LockEntry.KEY_FIELD});
  }

  /**
   * If there is a lock in the database with the same key, updates it if either is expired or both share the same owner.
   * If there is no lock with the same key, it's inserted.
   *
   * @param newLock lock to replace the existing one or be inserted.
   * @throws LockPersistenceException if there is a lock in database with same key, but is expired and belong to
   *                                  another owner or cannot insert/update the lock for any other reason
   */
  public void insertUpdate(LockEntry newLock)  {
    insertUpdate(newLock, false);
  }

  /**
   * If there is a lock in the database with the same key and owner, updates it.Otherwise throws a LockPersistenceException
   *
   * @param newLock lock to replace the existing one.
   * @throws LockPersistenceException if there is no lock in the database with the same key and owner or cannot update
   *                                  the lock for any other reason
   */
  public void updateIfSameOwner(LockEntry newLock)  {
    insertUpdate(newLock, true);
  }

  /**
   * Retrieves a lock by key
   *
   * @param lockKey key
   * @return LockEntry
   */
  public LockEntry findByKey(String lockKey) {
    Document result = collection.find(new Document().append(LockEntry.KEY_FIELD, lockKey)).first();
    if (result != null) {
      return new LockEntry(
          result.getString(LockEntry.KEY_FIELD),
          result.getString(LockEntry.STATUS_FIELD),
          result.getString(LockEntry.OWNER_FIELD),
          result.getDate(LockEntry.EXPIRES_AT_FIELD)
      );
    }
    return null;
  }

  /**
   * Removes from database all the locks with the same key(only can be one) and owner
   *
   * @param lockKey lock key
   * @param owner   lock owner
   */
  public void removeByKeyAndOwner(String lockKey, String owner) {
    collection
        .deleteMany(Filters.and(Filters.eq(LockEntry.KEY_FIELD, lockKey), Filters.eq(LockEntry.OWNER_FIELD, owner)));
  }

  /**
   * When onlyIfSameOwner == true, if(and only if) there is an existing lock for the same owner(and key, obviously),
   * it will update it. Won't insert a new lock. If there is a not-expired-lock for the same key, but not same owner,
   * will throw an exception.
   *
   * When onlyIfSameOwner == false, if there is a lock for the same owner, it will update it with the new values.
   * If there is no lock for the same key and owner, will insert newLock.
   * If there is already a not-expired-lock for the same key, which belongs to another owner, it will throw an exception.
   * This last case can bbe confused, as it will try to insert the lock(looking at the update condition 'acquireLockQuery'),
   * however, will rely on the key, so Mongo Driver will throw an MongoWriteException. This will be understood as
   * the lock is held(ex.getError().getCategory() == ErrorCategory.DUPLICATE_KEY)
   *
   * @param newLock newLock to be inserted/updated
   * @param onlyIfSameOwner if true, it requires an existing lock with the same owner
   */
  private void insertUpdate(LockEntry newLock, boolean onlyIfSameOwner)  {
    boolean lockHeld;

    Bson acquireLockQuery = getAcquireLockQuery(newLock.getKey(), newLock.getOwner(), onlyIfSameOwner);
    String debErrorDetail = "not db error";
    try {

      UpdateResult result = collection.updateMany(
          acquireLockQuery,
          new Document().append("$set", newLock.buildFullDBObject()),
          new UpdateOptions().upsert(!onlyIfSameOwner));

      lockHeld = result.getModifiedCount() <= 0 && result.getUpsertedId() == null;

    } catch (MongoWriteException ex) {
      lockHeld = ex.getError().getCategory() == ErrorCategory.DUPLICATE_KEY;
      if (!lockHeld) {
        throw ex;
      }
      debErrorDetail = ex.getError().toString();

    } catch (DuplicateKeyException ex) {
      lockHeld = true;
      debErrorDetail = ex.getMessage();
    }

    if (lockHeld) {
      throw new LockPersistenceException(
          acquireLockQuery.toString(),
          newLock.buildFullDBObject().toString(),
          debErrorDetail
      );
    }
  }


  private Bson getAcquireLockQuery(String lockKey, String owner, boolean onlyIfSameOwner) {
    final Bson alreadyExpiredCond = Filters.lt(LockEntry.EXPIRES_AT_FIELD, new Date());
    final Bson ownerCond = Filters.eq(LockEntry.OWNER_FIELD, owner);
    final Bson orCond = onlyIfSameOwner ? Filters.or(ownerCond) : Filters.or(alreadyExpiredCond, ownerCond);
    return Filters
        .and(Filters.eq(LockEntry.KEY_FIELD, lockKey), Filters.eq(LockEntry.STATUS_FIELD, LockStatus.LOCK_HELD.name()),
            orCond);
  }

}
