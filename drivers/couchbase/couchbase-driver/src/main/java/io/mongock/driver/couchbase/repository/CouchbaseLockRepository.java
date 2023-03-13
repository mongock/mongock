package io.mongock.driver.couchbase.repository;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.RemoveOptions;
import com.couchbase.client.java.kv.ReplaceOptions;
import io.mongock.driver.core.lock.LockEntry;
import io.mongock.driver.core.lock.LockPersistenceException;
import io.mongock.driver.core.lock.LockRepositoryWithEntity;
import io.mongock.driver.couchbase.lock.CouchbaseLockEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;

/**
 * Couchbase lock repository. 
 * Uses simple KV operations to store lock entries. For lock replacement uses Couchbase CAS (Optimistic Locking).
 *
 * @author Tigran Babloyan
 */
public class CouchbaseLockRepository extends CouchbaseRepositoryBase<LockEntry> implements LockRepositoryWithEntity<JsonObject> {
  private final static Logger logger = LoggerFactory.getLogger(CouchbaseLockRepository.class);
  public CouchbaseLockRepository(Cluster cluster, Collection collection) {
    super(cluster, collection, Collections.emptySet());
  }

  @Override
  public void insertUpdate(LockEntry newLock) throws LockPersistenceException {
    try{
      GetResult result = collection.get(newLock.getKey());
      LockEntry existingLock = new CouchbaseLockEntry(result.contentAsObject());
      if(newLock.getOwner().equals(existingLock.getOwner()) ||
          new Date().after(existingLock.getExpiresAt())){
        logger.debug("Lock with key {} already owned by us or is expired, so trying to perform a lock.", existingLock.getKey());
        collection.replace(newLock.getKey(), newLock, ReplaceOptions.replaceOptions().cas(result.cas()));
        logger.debug("Lock with key {} updated", newLock.getKey());
      } else if (new Date().before(existingLock.getExpiresAt())){
        logger.debug("Already locked by {}, will expire at", existingLock.getOwner(), existingLock.getExpiresAt());
        throw new LockPersistenceException("Get By" + newLock.getKey(), newLock.toString(), "Still locked by " + existingLock.getOwner() + " until " + existingLock.getExpiresAt());
      }
    } catch (DocumentNotFoundException documentNotFoundException){
      logger.debug("Lock with key {} does not exist, so trying to perform a lock.", newLock.getKey());
      collection.insert(newLock.getKey(), newLock);
      logger.debug("Lock with key {} created", newLock.getKey());
    }
  }

  @Override
  public void updateIfSameOwner(LockEntry newLock) throws LockPersistenceException {
    try{
      GetResult result = collection.get(newLock.getKey());
      LockEntry existingLock = new CouchbaseLockEntry(result.contentAsObject());
      if(newLock.getOwner().equals(existingLock.getOwner())){
        logger.debug("Lock with key {} already owned by us, so trying to perform a lock.", existingLock.getKey());
        collection.replace(newLock.getKey(), newLock, ReplaceOptions.replaceOptions().cas(result.cas()));
        logger.debug("Lock with key {} updated", newLock.getKey());
      } else {
        logger.debug("Already locked by {}, will expire at", existingLock.getOwner(), existingLock.getExpiresAt());
        throw new LockPersistenceException("Get By" + newLock.getKey(), newLock.toString(), "Lock belongs to " + existingLock.getOwner());  
      }
    } catch (DocumentNotFoundException documentNotFoundException){
      throw new LockPersistenceException("Get By" + newLock.getKey(), newLock.toString(), documentNotFoundException.getMessage());
    }
  }

  @Override
  public LockEntry findByKey(String lockKey) {
    try{
      GetResult result = collection.get(lockKey);
      return new CouchbaseLockEntry(result.contentAsObject());
    } catch (DocumentNotFoundException documentNotFoundException) {
      logger.debug("Lock for key {} was not found.", lockKey);
      return null;  
    }
  }

  @Override
  public void removeByKeyAndOwner(String lockKey, String owner) {
    try{
      GetResult result = collection.get(lockKey);
      LockEntry existingLock = new CouchbaseLockEntry(result.contentAsObject());
      if(owner.equals(existingLock.getOwner())){
        logger.debug("Lock for key {} belongs to us, so removing.", lockKey);
        collection.remove(lockKey, RemoveOptions.removeOptions().cas(result.cas()));
      } else {
        logger.debug("Lock for key {} belongs to other owner, can not delete.", lockKey);
      }
    } catch (DocumentNotFoundException documentNotFoundException) {
      logger.debug("Lock for key {} is not found, nothing to do", lockKey);
    }
  }
}
