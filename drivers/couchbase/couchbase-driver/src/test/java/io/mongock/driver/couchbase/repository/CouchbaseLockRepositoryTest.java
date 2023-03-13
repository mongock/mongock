package io.mongock.driver.couchbase.repository;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.PersistTo;
import com.couchbase.client.java.kv.RemoveOptions;
import com.couchbase.client.java.kv.ReplicateTo;
import io.mongock.driver.core.lock.LockEntry;
import io.mongock.driver.core.lock.LockPersistenceException;
import io.mongock.driver.couchbase.TestcontainersCouchbaseRunner;
import io.mongock.driver.couchbase.lock.CouchbaseLockEntry;
import io.mongock.driver.couchbase.util.LockEntryProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

class CouchbaseLockRepositoryTest {
  
  @AfterEach
  void cleanUp() {
    new CouchbaseLockRepository(TestcontainersCouchbaseRunner.getCluster6(), TestcontainersCouchbaseRunner.getCollectionV6()).deleteAll();
    new CouchbaseLockRepository(TestcontainersCouchbaseRunner.getCluster7(), TestcontainersCouchbaseRunner.getCollectionV7()).deleteAll();
    if(TestcontainersCouchbaseRunner.getCollectionV6().exists(LockEntryProvider.LOCK_KEY).exists()){
      TestcontainersCouchbaseRunner.getCollectionV6().remove(LockEntryProvider.LOCK_KEY, RemoveOptions.removeOptions().durability(PersistTo.ACTIVE, ReplicateTo.NONE));  
    }
    if(TestcontainersCouchbaseRunner.getCollectionV7().exists(LockEntryProvider.LOCK_KEY).exists()){
      TestcontainersCouchbaseRunner.getCollectionV7().remove(LockEntryProvider.LOCK_KEY, RemoveOptions.removeOptions().durability(PersistTo.ACTIVE, ReplicateTo.NONE));
    }
  }

  @BeforeAll
  static void initialize() {
    new CouchbaseLockRepository(TestcontainersCouchbaseRunner.getCluster6(), TestcontainersCouchbaseRunner.getCollectionV6()).initialize();
    new CouchbaseLockRepository(TestcontainersCouchbaseRunner.getCluster7(), TestcontainersCouchbaseRunner.getCollectionV7()).initialize();
  }

  @ParameterizedTest
  @DisplayName("insertUpdate: should insert a new lock if there is no existing lock")
  @MethodSource("io.mongock.driver.couchbase.util.LockEntryProvider#lockOwner1NotExpired")
  void test_insert_update_with_no_existing_lock(Cluster cluster, Collection collection, LockEntry lockOwner1NotExpired) {
    // given
    CouchbaseLockRepository lockRepository = new CouchbaseLockRepository(cluster, collection);
    
    // when
    lockRepository.insertUpdate(lockOwner1NotExpired);
    
    // then    
    CouchbaseLockEntry lockEntry = new CouchbaseLockEntry(collection.get(LockEntryProvider.LOCK_KEY).contentAsObject());
    assertEquals(lockOwner1NotExpired.getExpiresAt(), lockEntry.getExpiresAt());
    assertEquals(lockOwner1NotExpired.getOwner(), lockEntry.getOwner());
    assertEquals(lockOwner1NotExpired.getStatus(), lockEntry.getStatus());
    assertEquals(lockOwner1NotExpired.getKey(), lockEntry.getKey());
    assertNotNull(lockEntry.getDocType());
  }

  @ParameterizedTest
  @DisplayName("insertUpdate: should update an existing lock if there is an existing lock with the same owner")
  @MethodSource("io.mongock.driver.couchbase.util.LockEntryProvider#lockOwner1NotExpiredAndLockOwner1NotExpiredUpdated")
  void test_insert_update_with_existing_lock_same_owner(Cluster cluster, Collection collection, LockEntry lockOwner1NotExpired, LockEntry lockOwner1NotExpiredUpdated) {
    // given
    CouchbaseLockRepository lockRepository = new CouchbaseLockRepository(cluster, collection);
    lockRepository.insertUpdate(lockOwner1NotExpired);

    // when
    lockRepository.insertUpdate(lockOwner1NotExpiredUpdated);

    // then    
    LockEntry lockEntry = new CouchbaseLockEntry(collection.get(LockEntryProvider.LOCK_KEY).contentAsObject());
    assertEquals(lockOwner1NotExpiredUpdated.getExpiresAt(), lockEntry.getExpiresAt());
    assertEquals(lockOwner1NotExpiredUpdated.getOwner(), lockEntry.getOwner());
    assertEquals(lockOwner1NotExpiredUpdated.getStatus(), lockEntry.getStatus());
    assertEquals(lockOwner1NotExpiredUpdated.getKey(), lockEntry.getKey());
  }

  @ParameterizedTest
  @DisplayName("insertUpdate: should replace an existing lock if there is an existing lock with a different owner but the lock is expired")
  @MethodSource("io.mongock.driver.couchbase.util.LockEntryProvider#lockOwner1ExpiredAndLockOwner2NotExpired")
  void test_insert_update_with_existing_lock_other_owner_expired(Cluster cluster, Collection collection, LockEntry lockOwner1Expired, LockEntry lockOwner2NotExpired) {
    // given
    CouchbaseLockRepository lockRepository = new CouchbaseLockRepository(cluster, collection);
    lockRepository.insertUpdate(lockOwner1Expired);

    // when
    lockRepository.insertUpdate(lockOwner2NotExpired);

    // then    
    LockEntry lockEntry = new CouchbaseLockEntry(collection.get(LockEntryProvider.LOCK_KEY).contentAsObject());
    assertEquals(lockOwner2NotExpired.getExpiresAt(), lockEntry.getExpiresAt());
    assertEquals(lockOwner2NotExpired.getOwner(), lockEntry.getOwner());
    assertEquals(lockOwner2NotExpired.getStatus(), lockEntry.getStatus());
    assertEquals(lockOwner2NotExpired.getKey(), lockEntry.getKey());
  }

  @ParameterizedTest
  @DisplayName("insertUpdate: should throw LockPersistenceException if there is an existing lock with a different owner and the lock is not expired")
  @MethodSource("io.mongock.driver.couchbase.util.LockEntryProvider#lockOwner1NotExpiredAndLockOwner2NotExpired")
  void test_insert_update_should_throw_LockPersistenceException_if_locked_by_other_owner(Cluster cluster, Collection collection, LockEntry lockOwner1NotExpired, LockEntry lockOwner2NotExpired) {
    // given
    CouchbaseLockRepository lockRepository = new CouchbaseLockRepository(cluster, collection);
    lockRepository.insertUpdate(lockOwner2NotExpired);

    // when
    assertThrows(LockPersistenceException.class, () -> lockRepository.insertUpdate(lockOwner1NotExpired));
  }

  @ParameterizedTest
  @DisplayName("updateIfSameOwner: should throw LockPersistenceException if there is no existing lock")
  @MethodSource("io.mongock.driver.couchbase.util.LockEntryProvider#lockOwner1NotExpired")
  void test_update_if_same_owner_should_throw_LockPersistenceException_if_no_existing_lock(Cluster cluster, Collection collection, LockEntry lockOwner1NotExpired) {
    // given
    CouchbaseLockRepository lockRepository = new CouchbaseLockRepository(cluster, collection);

    // when
    assertThrows(LockPersistenceException.class, () -> lockRepository.updateIfSameOwner(lockOwner1NotExpired));
  }

  @ParameterizedTest
  @DisplayName("updateIfSameOwner: should throw LockPersistenceException if there is an existing lock with a different owner")
  @MethodSource("io.mongock.driver.couchbase.util.LockEntryProvider#lockOwner1NotExpiredAndLockOwner2NotExpired")
  void test_update_if_same_owner_should_throw_LockPersistenceException_if_locked_by_other_owner(Cluster cluster, Collection collection, LockEntry lockOwner1NotExpired, LockEntry lockOwner2NotExpired) {
    // given
    CouchbaseLockRepository lockRepository = new CouchbaseLockRepository(cluster, collection);
    lockRepository.insertUpdate(lockOwner2NotExpired);

    // when
    assertThrows(LockPersistenceException.class, () -> lockRepository.updateIfSameOwner(lockOwner1NotExpired));
  }

  @ParameterizedTest
  @DisplayName("updateIfSameOwner: should update the lock if there is an existing lock with the same owner")
  @MethodSource("io.mongock.driver.couchbase.util.LockEntryProvider#lockOwner1NotExpiredAndLockOwner1NotExpiredUpdated")
  void test_update_if_same_owner_should_update_if_locked_by_same_owner(Cluster cluster, Collection collection, LockEntry lockOwner1NotExpired, LockEntry lockOwner1NotExpiredUpdated) {
    // given
    CouchbaseLockRepository lockRepository = new CouchbaseLockRepository(cluster, collection);
    lockRepository.insertUpdate(lockOwner1NotExpired);

    // when
    lockRepository.updateIfSameOwner(lockOwner1NotExpiredUpdated);

    // then    
    LockEntry lockEntry = new CouchbaseLockEntry(collection.get(LockEntryProvider.LOCK_KEY).contentAsObject());
    assertEquals(lockOwner1NotExpiredUpdated.getExpiresAt(), lockEntry.getExpiresAt());
    assertEquals(lockOwner1NotExpiredUpdated.getOwner(), lockEntry.getOwner());
    assertEquals(lockOwner1NotExpiredUpdated.getStatus(), lockEntry.getStatus());
    assertEquals(lockOwner1NotExpiredUpdated.getKey(), lockEntry.getKey());
  }

  @ParameterizedTest
  @DisplayName("updateIfSameOwner: should update the lock if there is an existing lock with the same owner but the lock is expired")
  @MethodSource("io.mongock.driver.couchbase.util.LockEntryProvider#lockOwner1ExpiredAndLockOwner1NotExpiredUpdated")
  void test_update_if_same_owner_should_update_if_locked_by_same_owner_and_expired(Cluster cluster, Collection collection, LockEntry lockOwner1Expired, LockEntry lockOwner1NotExpiredUpdated) {
    // given
    CouchbaseLockRepository lockRepository = new CouchbaseLockRepository(cluster, collection);
    lockRepository.insertUpdate(lockOwner1Expired);

    // when
    lockRepository.updateIfSameOwner(lockOwner1NotExpiredUpdated);

    // then    
    LockEntry lockEntry = new CouchbaseLockEntry(collection.get(LockEntryProvider.LOCK_KEY).contentAsObject());
    assertEquals(lockOwner1NotExpiredUpdated.getExpiresAt(), lockEntry.getExpiresAt());
    assertEquals(lockOwner1NotExpiredUpdated.getOwner(), lockEntry.getOwner());
    assertEquals(lockOwner1NotExpiredUpdated.getStatus(), lockEntry.getStatus());
    assertEquals(lockOwner1NotExpiredUpdated.getKey(), lockEntry.getKey());
  }

  @ParameterizedTest
  @DisplayName("findByKey: should return null if missing key is provided")
  @MethodSource("io.mongock.driver.couchbase.util.LockEntryProvider#clusterAndCollection")
  void test_find_by_key_missing(Cluster cluster, Collection collection) {
    // given
    CouchbaseLockRepository lockRepository = new CouchbaseLockRepository(cluster, collection);

    // when
    LockEntry lockEntry = lockRepository.findByKey("SOME_RANDOM_KEY");

    // then
    assertNull(lockEntry);
  }

  @ParameterizedTest
  @DisplayName("findByKey: should return correct LockEntry for given key")
  @MethodSource("io.mongock.driver.couchbase.util.LockEntryProvider#lockOwner1NotExpired")
  void test_find_by_key_existing(Cluster cluster, Collection collection, LockEntry lockOwner1NotExpired) {
    // given
    CouchbaseLockRepository lockRepository = new CouchbaseLockRepository(cluster, collection);
    lockRepository.insertUpdate(lockOwner1NotExpired);

    // when
    LockEntry lockEntry = lockRepository.findByKey(LockEntryProvider.LOCK_KEY);

    // then
    assertNotNull(lockEntry);
    assertEquals(lockOwner1NotExpired.getExpiresAt(), lockEntry.getExpiresAt());
    assertEquals(lockOwner1NotExpired.getOwner(), lockEntry.getOwner());
    assertEquals(lockOwner1NotExpired.getStatus(), lockEntry.getStatus());
    assertEquals(lockOwner1NotExpired.getKey(), lockEntry.getKey());
  }

  @ParameterizedTest
  @DisplayName("removeByKeyAndOwner: should delete lock if there lock key and owner matches")
  @MethodSource("io.mongock.driver.couchbase.util.LockEntryProvider#lockOwner1NotExpired")
  void test_remove_by_key_and_owner_should_delete_if_lock_matches(Cluster cluster, Collection collection, LockEntry lockOwner1NotExpired) {
    // given
    CouchbaseLockRepository lockRepository = new CouchbaseLockRepository(cluster, collection);
    lockRepository.insertUpdate(lockOwner1NotExpired);

    // when
    lockRepository.removeByKeyAndOwner(LockEntryProvider.LOCK_KEY, lockOwner1NotExpired.getOwner());

    // then
    assertFalse(collection.exists(LockEntryProvider.LOCK_KEY).exists());
  }

  @ParameterizedTest
  @DisplayName("removeByKeyAndOwner: should do nothing if there is owner does not match")
  @MethodSource("io.mongock.driver.couchbase.util.LockEntryProvider#lockOwner1NotExpiredAndLockOwner2NotExpired")
  void test_remove_by_key_and_owner_should_do_nothing_if_lock__owner_not_matches(Cluster cluster, Collection collection, LockEntry lockOwner1NotExpired, LockEntry lockOwner2NotExpired) {
    // given
    CouchbaseLockRepository lockRepository = new CouchbaseLockRepository(cluster, collection);
    lockRepository.insertUpdate(lockOwner2NotExpired);

    // when
    lockRepository.removeByKeyAndOwner(LockEntryProvider.LOCK_KEY, lockOwner1NotExpired.getOwner());

    // then
    assertTrue(collection.exists(LockEntryProvider.LOCK_KEY).exists());
  }
  
}
