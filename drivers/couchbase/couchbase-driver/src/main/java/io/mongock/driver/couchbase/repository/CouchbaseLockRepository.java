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
import io.mongock.utils.field.FieldInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Couchbase lock repository. 
 * Uses simple KV operations to store lock entries. For lock replacement uses Couchbase CAS (Optimistic Locking).
 *
 * @author Tigran Babloyan
 */
public class CouchbaseLockRepository extends CouchbaseRepositoryBase<LockEntry> implements LockRepositoryWithEntity<JsonObject> {
  private static final Logger logger = LoggerFactory.getLogger(CouchbaseLockRepository.class);
  
  private LockEntryKeyGenerator keyGenerator = new LockEntryKeyGenerator();
  
  public CouchbaseLockRepository(Cluster cluster, Collection collection) {
    super(cluster, collection, Collections.emptySet());
  }

  @Override
  public void insertUpdate(LockEntry newLock) throws LockPersistenceException {
    String key = keyGenerator.toKey(newLock);
    try{
      GetResult result = collection.get(key);
      LockEntry existingLock = new CouchbaseLockEntry(result.contentAsObject());
      if(newLock.getOwner().equals(existingLock.getOwner()) ||
          new Date().after(existingLock.getExpiresAt())){
        logger.debug("Lock with key {} already owned by us or is expired, so trying to perform a lock.", existingLock.getKey());
        collection.replace(key, toEntity(newLock), ReplaceOptions.replaceOptions().cas(result.cas()));
        logger.debug("Lock with key {} updated", key);
      } else if (new Date().before(existingLock.getExpiresAt())){
        logger.debug("Already locked by {}, will expire at {}", existingLock.getOwner(), existingLock.getExpiresAt());
        throw new LockPersistenceException("Get By" + key, newLock.toString(), "Still locked by " + existingLock.getOwner() + " until " + existingLock.getExpiresAt());
      }
    } catch (DocumentNotFoundException documentNotFoundException){
      logger.debug("Lock with key {} does not exist, so trying to perform a lock.", newLock.getKey());
      collection.insert(key, toEntity(newLock));
      logger.debug("Lock with key {} created", key);
    }
  }

  @Override
  public void updateIfSameOwner(LockEntry newLock) throws LockPersistenceException {
    String key = keyGenerator.toKey(newLock);
    try{
      GetResult result = collection.get(key);
      LockEntry existingLock = new CouchbaseLockEntry(result.contentAsObject());
      if(newLock.getOwner().equals(existingLock.getOwner())){
        logger.debug("Lock with key {} already owned by us, so trying to perform a lock.", existingLock.getKey());
        collection.replace(key, toEntity(newLock), ReplaceOptions.replaceOptions().cas(result.cas()));
        logger.debug("Lock with key {} updated", key);
      } else {
        logger.debug("Already locked by {}, will expire at {}", existingLock.getOwner(), existingLock.getExpiresAt());
        throw new LockPersistenceException("Get By " + key, newLock.toString(), "Lock belongs to " + existingLock.getOwner());  
      }
    } catch (DocumentNotFoundException documentNotFoundException){
      throw new LockPersistenceException("Get By " + key, newLock.toString(), documentNotFoundException.getMessage());
    }
  }

  @Override
  public LockEntry findByKey(String lockKey) {
    String key = keyGenerator.toKey(lockKey);
    try{
      GetResult result = collection.get(key);
      return new CouchbaseLockEntry(result.contentAsObject());
    } catch (DocumentNotFoundException documentNotFoundException) {
      logger.debug("Lock for key {} was not found.", key);
      return null;  
    }
  }

  @Override
  public void removeByKeyAndOwner(String lockKey, String owner) {
    String key = keyGenerator.toKey(lockKey);
    try{
      GetResult result = collection.get(key);
      LockEntry existingLock = new CouchbaseLockEntry(result.contentAsObject());
      if(owner.equals(existingLock.getOwner())){
        logger.debug("Lock for key {} belongs to us, so removing.", key);
        collection.remove(key, RemoveOptions.removeOptions().cas(result.cas()));
      } else {
        logger.debug("Lock for key {} belongs to other owner, can not delete.", key);
      }
    } catch (DocumentNotFoundException documentNotFoundException) {
      logger.debug("Lock for key {} is not found, nothing to do", key);
    }
  }

  @Override
  public JsonObject mapFieldInstances(List<FieldInstance> fieldInstanceList) {
    JsonObject document = super.mapFieldInstances(fieldInstanceList);
    document.put(DOCUMENT_TYPE_KEY, LockEntryKeyGenerator.DOCUMENT_TYPE_LOCK_ENTRY);
    return document;
  }
  
}
