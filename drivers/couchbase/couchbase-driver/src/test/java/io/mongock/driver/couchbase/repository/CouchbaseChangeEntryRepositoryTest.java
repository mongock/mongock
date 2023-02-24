package io.mongock.driver.couchbase.repository;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.driver.couchbase.TestcontainersCouchbaseRunner;
import io.mongock.driver.couchbase.entry.CouchbaseChangeEntry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CouchbaseChangeEntryRepositoryTest {
  private static final ChangeEntryKeyGenerator KEY_GENERATOR = new ChangeEntryKeyGenerator();


  @AfterEach
  void cleanUp() {
    new CouchbaseChangeEntryRepository(TestcontainersCouchbaseRunner.getCluster6(), TestcontainersCouchbaseRunner.getCollectionV6()).deleteAll();
    new CouchbaseChangeEntryRepository(TestcontainersCouchbaseRunner.getCluster7(), TestcontainersCouchbaseRunner.getCollectionV7()).deleteAll();
  }

  @BeforeAll
  static void initialize() {
    new CouchbaseLockRepository(TestcontainersCouchbaseRunner.getCluster6(), TestcontainersCouchbaseRunner.getCollectionV6()).initialize();
    new CouchbaseLockRepository(TestcontainersCouchbaseRunner.getCluster7(), TestcontainersCouchbaseRunner.getCollectionV7()).initialize();
  }

  @ParameterizedTest
  @DisplayName("saveOrUpdate: should save new change entry")
  @MethodSource("io.mongock.driver.couchbase.util.ChangeEntryProvider#changes")
  void test_save_or_update_new_change_entry(Cluster cluster, Collection collection, ChangeEntry change) {
    // given
    CouchbaseChangeEntryRepository changeEntryRepository = new CouchbaseChangeEntryRepository(cluster, collection);

    // when
    changeEntryRepository.saveOrUpdate(change);

    // then
    ChangeEntry changeEntry = new CouchbaseChangeEntry(collection.get(KEY_GENERATOR.toKey(change)).contentAsObject());
    assertEquals(changeEntry.getChangeId(), change.getChangeId());
    assertEquals(changeEntry.getAuthor(), change.getAuthor());
    assertEquals(changeEntry.getExecutionId(), change.getExecutionId());
    assertEquals(changeEntry.getChangeLogClass(), change.getChangeLogClass());
    assertEquals(changeEntry.getChangeSetMethod(), change.getChangeSetMethod());
    // ensure that custom field types are correctly stored and retrieved
    assertEquals(changeEntry.getErrorTrace(), change.getErrorTrace());
    assertEquals(changeEntry.getTimestamp(), change.getTimestamp());
  }

  @ParameterizedTest
  @DisplayName("saveOrUpdate: should update change entry if already exists")
  @MethodSource("io.mongock.driver.couchbase.util.ChangeEntryProvider#change1AndChange1U")
  void test_save_or_update_update_existing_change_entry(Cluster cluster, Collection collection, ChangeEntry change, ChangeEntry updatedChange) {
    // given
    CouchbaseChangeEntryRepository changeEntryRepository = new CouchbaseChangeEntryRepository(cluster, collection);
    changeEntryRepository.saveOrUpdate(change);

    // when
    changeEntryRepository.saveOrUpdate(updatedChange);

    // then
    ChangeEntry changeEntry = new CouchbaseChangeEntry(collection.get(KEY_GENERATOR.toKey(change)).contentAsObject());
    assertEquals(changeEntry.getChangeId(), updatedChange.getChangeId());
    assertEquals(changeEntry.getAuthor(), updatedChange.getAuthor());
    assertEquals(changeEntry.getExecutionId(), updatedChange.getExecutionId());
    assertEquals(changeEntry.getChangeLogClass(), updatedChange.getChangeLogClass());
    assertEquals(changeEntry.getChangeSetMethod(), updatedChange.getChangeSetMethod());
    // ensure that custom field types are correctly stored and retrieved
    assertEquals(changeEntry.getErrorTrace(), updatedChange.getErrorTrace());
    assertEquals(changeEntry.getTimestamp(), updatedChange.getTimestamp());
  }

  @ParameterizedTest
  @DisplayName("getEntriesLog: should return all entries")
  @MethodSource("io.mongock.driver.couchbase.util.ChangeEntryProvider#changesAsList")
  void test_save_or_update_update_existing_change_entry(Cluster cluster, Collection collection, List<ChangeEntry> changes) {
    // given
    CouchbaseChangeEntryRepository changeEntryRepository = new CouchbaseChangeEntryRepository(cluster, collection);
    List<ChangeEntry> initialChanges = changeEntryRepository.getEntriesLog();
    assertEquals(0, initialChanges.size());

    // when
    changes.forEach(changeEntryRepository::saveOrUpdate);

    // then
    List<ChangeEntry> existingChanges = changeEntryRepository.getEntriesLog();
    assertEquals(changes.size(), existingChanges.size());
  }
}
