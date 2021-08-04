package com.github.cloudyrock.mongock.driver.mongodb.v3.repository;

import com.github.cloudyrock.mongock.driver.core.lock.LockEntry;
import com.github.cloudyrock.mongock.driver.core.lock.LockPersistenceException;
import com.github.cloudyrock.mongock.driver.core.lock.LockRepositoryWithEntity;
import com.github.cloudyrock.mongock.driver.core.lock.LockStatus;
import com.mongodb.DuplicateKeyException;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Date;

public class Mongo3LockRepository extends Mongo3RepositoryBase<LockEntry> implements LockRepositoryWithEntity<Document> {

  private static final String KEY_FIELD = "key";
  private static final String STATUS_FIELD = "status";
  private static final String OWNER_FIELD = "owner";
  private static final String EXPIRES_AT_FIELD = "expiresAt";


  public Mongo3LockRepository(MongoCollection<Document> collection,
                                  boolean indexCreation) {
    super(collection, new String[]{KEY_FIELD}, indexCreation, ReadWriteConfiguration.getDefault());
  }

  public Mongo3LockRepository(MongoCollection<Document> collection,
                                  boolean indexCreation,
                                  ReadWriteConfiguration readWriteConfiguration) {
    super(collection, new String[]{KEY_FIELD}, indexCreation, readWriteConfiguration);
  }
  /**
   * If there is a lock in the database with the same key, updates it if either is expired or both share the same owner.
   * If there is no lock with the same key, it's inserted.
   *
   * @param newLock lock to replace the existing one or be inserted.
   * @throws  if there is a lock in database with same key, but is expired and belong to
   *                                  another owner or cannot insert/update the lock for any other reason
   */
  @Override
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
    Document result = collection.find(new Document().append(KEY_FIELD, lockKey)).first();
    if (result != null) {
      return new LockEntry(
          result.getString(KEY_FIELD),
          result.getString(STATUS_FIELD),
          result.getString(OWNER_FIELD),
          result.getDate(EXPIRES_AT_FIELD)
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
    collection.deleteMany(Filters.and(Filters.eq(KEY_FIELD, lockKey), Filters.eq(OWNER_FIELD, owner)));
  }

  @Override
  public void deleteAll() {
    collection.deleteMany(new BsonDocument());
  }

  protected void insertUpdate(LockEntry newLock, boolean onlyIfSameOwner)  {
    boolean lockHeld;
    String debErrorDetail = "not db error";
    Bson acquireLockQuery = getAcquireLockQuery(newLock.getKey(), newLock.getOwner(), onlyIfSameOwner);
    Document newLockDocumentSet = new Document().append("$set", toEntity(newLock));
    try {
      UpdateResult result = collection.updateMany(acquireLockQuery, newLockDocumentSet, new UpdateOptions().upsert(!onlyIfSameOwner));
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
          newLockDocumentSet.toString(),
          debErrorDetail
      );
    }
  }

  protected Bson getAcquireLockQuery(String lockKey, String owner, boolean onlyIfSameOwner) {
    Bson alreadyExpiredCond = Filters.lt(EXPIRES_AT_FIELD, new Date());
    Bson ownerCond = Filters.eq(OWNER_FIELD, owner);
    Bson orCond = onlyIfSameOwner ? Filters.or(ownerCond) : Filters.or(alreadyExpiredCond, ownerCond);
    return Filters.and(Filters.eq(KEY_FIELD, lockKey), Filters.eq(STATUS_FIELD, LockStatus.LOCK_HELD.toString()), orCond);
  }
}
