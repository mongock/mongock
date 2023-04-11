package io.mongock.driver.couchbase.driver;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import io.mongock.driver.api.entry.ChangeEntryService;
import io.mongock.driver.core.driver.NonTransactionalConnectionDriverBase;
import io.mongock.driver.core.lock.LockRepository;
import io.mongock.driver.couchbase.repository.CouchbaseChangeEntryRepository;
import io.mongock.driver.couchbase.repository.CouchbaseLockRepository;

import static io.mongock.utils.Constants.*;

/**
 * NonTransactionalConnectionDriverBase implementation for Couchbase.
 * 
 * Note: Transactions are not supported for backwards compatibility with previous versions of Couchbase Servers (prior 6.6.1).
 * 
 * @author Tigran Babloyan
 */
public class CouchbaseDriver extends NonTransactionalConnectionDriverBase {

  private final Collection collection;
  private final Cluster cluster;
  private CouchbaseChangeEntryRepository changeEntryRepository;
  private CouchbaseLockRepository lockRepository;

  protected CouchbaseDriver(Cluster cluster, Collection collection, long lockAcquiredForMillis, long lockQuitTryingAfterMillis, long lockTryFrequencyMillis) {
    super(lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
    this.collection = collection;
    this.cluster = cluster;
  }

  @Override
  public ChangeEntryService getChangeEntryService() {
    if (changeEntryRepository == null) {
      changeEntryRepository = new CouchbaseChangeEntryRepository(cluster, collection);
      changeEntryRepository.setIndexCreation(isIndexCreation());
    }
    return changeEntryRepository;
  }

  @Override
  protected LockRepository getLockRepository() {
    if (lockRepository == null) {
      lockRepository = new CouchbaseLockRepository(cluster, collection);
      lockRepository.setIndexCreation(isIndexCreation());
    }
    return lockRepository;
  }

  public static CouchbaseDriver withDefaultLock(Cluster cluster, Collection collection) {
    return CouchbaseDriver.withLockStrategy(cluster, collection, DEFAULT_LOCK_ACQUIRED_FOR_MILLIS, DEFAULT_QUIT_TRYING_AFTER_MILLIS, DEFAULT_TRY_FREQUENCY_MILLIS);
  }

  public static CouchbaseDriver withLockStrategy(Cluster cluster,
                                                 Collection collection,
                                                 long lockAcquiredForMillis,
                                                 long lockQuitTryingAfterMillis,
                                                 long lockTryFrequencyMillis) {
    return new CouchbaseDriver(cluster, collection, lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
  }

}
