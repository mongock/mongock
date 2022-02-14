package io.mongock.driver.mongodb.reactive.repository;


import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.core.lock.LockEntry;
import io.mongock.driver.core.lock.LockPersistenceException;
import io.mongock.driver.core.lock.LockStatus;
import io.mongock.driver.mongodb.reactive.MongoDbReactiveDriverTestAdapterImpl;
import io.mongock.driver.mongodb.reactive.repository.template.LockRepositoryITestBase;
import io.mongock.driver.mongodb.reactive.util.IntegrationTestBase;
import io.mongock.driver.mongodb.reactive.util.MongoCollectionSync;
import io.mongock.driver.mongodb.reactive.util.MongoDBDriverTestAdapter;
import io.mongock.driver.mongodb.reactive.util.RepositoryAccessorHelper;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class MongoReactiveLockRepositoryITest extends IntegrationTestBase implements LockRepositoryITestBase {

  private static final String LOCK_KEY = "LOCK_KEY";

  protected MongoReactiveLockRepository repository;

  @Test
  public void shouldCreateDefaultReadWriteConcerns_whenCreating_ifNoParams() {
    //given then
    testReadWriteConcern(WriteConcern.MAJORITY.withJournal(true), ReadConcern.MAJORITY, ReadPreference.primary(), null);
  }

  @Test
  public void shouldPassedReadWriteConcerns_whenCreating_ifConfigurationIsPassed() {

    //given
    WriteConcern expectedWriteConcern = WriteConcern.W1;
    ReadConcern expectedReadConcern = ReadConcern.LINEARIZABLE;
    ReadPreference expectedReadPreference = ReadPreference.nearest();
    ReadWriteConfiguration readWriteConfiguration = new ReadWriteConfiguration(expectedWriteConcern, expectedReadConcern, expectedReadPreference);

    //then
    testReadWriteConcern(expectedWriteConcern, expectedReadConcern, expectedReadPreference, readWriteConfiguration);
  }

  // TODO TO REUSE

  @Test
  @DisplayName("SHOULD create unique index WHEN ensureIndex=true IF index not created")
  public void shouldCreateUniqueIndex_whenEnsureIndex_IfNotCreatedYet() throws MongockException {
    initializeRepository();
    //then
    verify(repository, times(1)).createRequiredUniqueIndex();
    // and not
    verify(repository, times(0)).dropIndex(any(Document.class));
  }

  @Test
  @DisplayName("SHOULD NO create unique index WHEN ensureIndex=true IF index created")
  public void shouldNoCreateUniqueIndex_whenEnsureIndex_IfAlreadyCreated() throws MongockException {
    initializeRepository();

    // given
    //TODO remove this. Already done in initializeRepository()
    MongoReactiveLockRepository repo = new MongoReactiveLockRepository(getDataBase().getCollection(LOCK_COLLECTION_NAME));
    repo.setIndexCreation(true);
    repository = Mockito.spy(repo);


    doReturn(true).when((MongoReactiveLockRepository) repository).isUniqueIndex(any(Document.class));

    // when
    repository.initialize();

    //then
    verify((MongoReactiveLockRepository) repository, times(0)).createRequiredUniqueIndex();
    // and not
    verify((MongoReactiveLockRepository) repository, times(0)).dropIndex(new Document());
  }

  @Test
  @DisplayName("Key should be unique")
  public void ensureKeyUniqueness() {
    initializeRepository();
    //inserting lock with key1: fine
    getAdapter(LOCK_COLLECTION_NAME)
        .insertOne(repository.toEntity(new LockEntry("KEY1", "STATUS1", "process1", new Date(System.currentTimeMillis() - 60000))));
    //inserting lock with key2: fine
    getAdapter(LOCK_COLLECTION_NAME)
        .insertOne(repository.toEntity(new LockEntry("KEY2", "STATUS1", "process1", new Date(System.currentTimeMillis() - 60000))));

    try {
      //inserting lock with key1 again: Exception
      getAdapter(LOCK_COLLECTION_NAME)
          .insertOne(repository.toEntity(new LockEntry("KEY1", "STATUS2", "process2", new Date(System.currentTimeMillis() - 60000))));

    } catch (MongoWriteException ex) {
      assertEquals(ErrorCategory.DUPLICATE_KEY, ex.getError().getCategory());
    }
  }

  @Test
  @DisplayName("SHOULD return lock WHEN findByKey IF it's present in DB")
  public void shouldReturnLockWhenFindByKeyIfItIsInDB() throws LockPersistenceException, MongockException {
    initializeRepository();
    //given
    MongoCollectionSync collection = new MongoCollectionSync(getDataBase().getCollection(LOCK_COLLECTION_NAME));
    collection.updateMany(
        new Document(),
        new Document().append("$set",
            repository.toEntity(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1", new Date(System.currentTimeMillis() - 60000)))),
        new UpdateOptions().upsert(true));

    //when
    final LockEntry result = repository.findByKey(LOCK_KEY);

    //then
    assertNotNull(result);
  }

  @Test
  @DisplayName("SHOULD insert LOCK WHEN insertUpdate IF collection/table is empty")
  public void insertUpdateShouldInsertWhenEmpty() throws LockPersistenceException, MongockException {
    initializeRepository();

    // when
    Date expiresAtExpected = new Date(System.currentTimeMillis() - 60000);
    repository.insertUpdate(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1", expiresAtExpected));

    //then
    MongoCollectionSync mongoCollection = new MongoCollectionSync(getDataBase().getCollection(LOCK_COLLECTION_NAME));
    List<Document> result = mongoCollection.find(new Document().append("key", LOCK_KEY));
    assertNotNull(result.get(0));
    assertEquals(expiresAtExpected, result.get(0).get("expiresAt"));
  }

  @Test
  @DisplayName("SHOULD update WHEN insertUpdate IF new lock and lock in DB have same key AND owner")
  public void insertUpdateShouldUpdateWhenSameOwner() throws LockPersistenceException, MongockException {
    initializeRepository();
    //given
    repository.insertUpdate(
        new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1",
            new Date(System.currentTimeMillis() + 60 * 60 * 1000)));

    //when
    Date expiresAtExpected = new Date();
    repository.insertUpdate(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1", expiresAtExpected));

    //then
    MongoCollectionSync mongoCollection = new MongoCollectionSync(getDataBase().getCollection(LOCK_COLLECTION_NAME));
    List<Document> result = mongoCollection.find(new Document().append("key", LOCK_KEY));
    assertNotNull(result.get(0));
    assertEquals(expiresAtExpected, result.get(0).get("expiresAt"));
  }

  @Test
  @DisplayName("SHOULD replace WHEN insertUpdate IF new lock and lock ind DB have same key AND different owner AND lock in DB is expired")
  public void insertUpdateShouldUpdateWhenLockIndDbIsExpired() throws LockPersistenceException, MongockException {
    initializeRepository();
    //given
    final long currentMillis = System.currentTimeMillis();
    repository
        .insertUpdate(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1", new Date(currentMillis - 1000)));

    //when
    Date expiresAtExpected = new Date();
    repository.insertUpdate(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process2", expiresAtExpected));

    //then
    MongoCollectionSync mongoCollection = new MongoCollectionSync(getDataBase().getCollection(LOCK_COLLECTION_NAME));
    List<Document> result = mongoCollection.find(new Document().append("key", LOCK_KEY));
    assertNotNull(result.get(0));
    assertEquals(expiresAtExpected, result.get(0).get("expiresAt"));

  }

  @Test
  @DisplayName("SHOULD throw exception WHEN insertUpdate IF new lock and lock ind DB have same key AND different owner AND lock in DB is NOT expired ")
  public void insertUpdateShouldThrowExceptionWhenLockIsInDBWIthDifferentOwnerAndNotExpired() throws LockPersistenceException, MongockException {
    initializeRepository();
    //given
    final long currentMillis = System.currentTimeMillis();
    repository
        .insertUpdate(
            new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1", new Date(currentMillis + 60 * 60 * 1000)));

    //when
    assertThrows(LockPersistenceException.class, () -> repository
        .insertUpdate(
            new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process2", new Date(currentMillis + 90 * 60 * 1000))));
  }

  @Test
  @DisplayName("SHOULD delete lock WHEN remove IF lock in DB belongs to the given owner")
  public void removeShouldRemoveWhenSameOwner() throws LockPersistenceException, MongockException {
    initializeRepository();
    //given
    MongoCollectionSync mongoCollection = new MongoCollectionSync(getDataBase().getCollection(LOCK_COLLECTION_NAME));
    mongoCollection.updateMany(
        new Document(),
        new Document().append("$set",
            repository.toEntity(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1",
                new Date(System.currentTimeMillis() - 600000)))),
        new UpdateOptions().upsert(true));

    assertNotNull(mongoCollection.find(new Document().append("key", LOCK_KEY)).get(0));

    //when
    repository.removeByKeyAndOwner(LOCK_KEY, "process1");

    //then
    assertNull(mongoCollection.find(new Document().append("key", LOCK_KEY)).first());
  }

  @Test
  @DisplayName("SHOULD NOT delete lock WHEN remove IF lock does NOT belong to the given owner")
  public void removeShouldNotRemoveWhenDifferentOwner() throws LockPersistenceException, MongockException {
    initializeRepository();
    //given
    MongoCollectionSync mongoCollection = new MongoCollectionSync(getDataBase().getCollection(LOCK_COLLECTION_NAME));

    mongoCollection.updateMany(
        new Document(),
        new Document().append("$set",
            repository.toEntity(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1",
                new Date(System.currentTimeMillis() - 600000)))),
        new UpdateOptions().upsert(true));

    assertNotNull(mongoCollection.find(new Document().append("key", LOCK_KEY)).get(0));

    //when
    repository.removeByKeyAndOwner(LOCK_KEY, "process2");

    //then
    assertNotNull(getDataBase().getCollection(LOCK_COLLECTION_NAME).find(new Document().append("key", LOCK_KEY)).first());
  }

  @Test
  @DisplayName("SHOULD NOT insert WHEN updateIfSameOwner IF there is no lock in DB for the given key and owner")
  public void updateIfSameOwnerShouldNotInsertWhenEmpty() throws LockPersistenceException, MongockException {
    initializeRepository();
    //when
    assertThrows(LockPersistenceException.class, () -> repository.updateIfSameOwner(
        new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1",
            new Date(System.currentTimeMillis() - 600000))));
  }

  @Test
  @DisplayName("SHOULD NOT update WHEN updateIfSameOwner IF current lock in DB is expired BUT new lock and lock in DB don't share owner")
  public void updateIfSameOwnerShouldNotUpdateWhenExpiresAtIsGraterThanSavedButOtherOwner() throws LockPersistenceException, MongockException {
    initializeRepository();
    //given
    final long currentMillis = System.currentTimeMillis();
    repository
        .insertUpdate(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1", new Date(currentMillis - 1000)));

    //when
    assertThrows(LockPersistenceException.class, () -> repository
        .updateIfSameOwner(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process2", new Date(currentMillis))));

  }

  @Test
  @DisplayName("SHOULD update WHEN updateIfSameOwner IF new lock and lock in DB  share owner")
  public void updateIfSameOwnerShouldUpdateWhenSameOwner() throws LockPersistenceException, MongockException {
    initializeRepository();
    //given
    repository.insertUpdate(
        new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1",
            new Date(System.currentTimeMillis() + 60 * 60 * 1000)));

    //when
    Date expiresAtExpected = new Date(System.currentTimeMillis());
    repository.updateIfSameOwner(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1", expiresAtExpected));

    //then
    MongoCollectionSync mongoCollection = new MongoCollectionSync(getDataBase().getCollection(LOCK_COLLECTION_NAME));
    List<Document> result = mongoCollection.find(new Document().append("key", LOCK_KEY));
    assertNotNull(result.get(0));
    assertEquals(expiresAtExpected, result.get(0).get("expiresAt"));
  }

  @Test
  @DisplayName("SHOULD NOT update WHEN updateIfSameOwner IF new lock and lock in DB don't have same owner and lock in DB is not expired")
  public void updateIfSameOwnerShouldNotUpdateWhenDifferentOwnerAndExpiresAtIsNotGrater() throws LockPersistenceException, MongockException {
    repository = new MongoReactiveLockRepository(getDataBase().getCollection(LOCK_COLLECTION_NAME));
    repository.setIndexCreation(true);
    repository.initialize();
    // given
    final long currentMillis = System.currentTimeMillis();
    repository
        .insertUpdate(
            new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1", new Date(currentMillis + 60 * 60 * 1000)));

    // when
    assertThrows(LockPersistenceException.class, () -> repository.insertUpdate(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process2", new Date(currentMillis))));
  }








  private void testReadWriteConcern(WriteConcern expectedWriteConcern, ReadConcern expectedReadConcern, ReadPreference expectedReadPreference, ReadWriteConfiguration readWriteConfiguration) {
    MongoReactiveLockRepository repo;
    if (readWriteConfiguration != null) {
      repo = new MongoReactiveLockRepository(getDataBase().getCollection(LOCK_COLLECTION_NAME), readWriteConfiguration);
    } else {
      repo = new MongoReactiveLockRepository(getDataBase().getCollection(LOCK_COLLECTION_NAME));
    }
    MongoCollection<Document> collection = RepositoryAccessorHelper.getCollection(repo).getCollection();
    assertEquals(expectedWriteConcern, collection.getWriteConcern());
    assertEquals(expectedReadConcern, collection.getReadConcern());
    assertEquals(expectedReadPreference, collection.getReadPreference());
  }


  public void initializeRepository() {
    MongoReactiveLockRepository repo = new MongoReactiveLockRepository(getDataBase().getCollection(LOCK_COLLECTION_NAME));
    repo.setIndexCreation(true);
    repository = Mockito.spy(repo);
    repository.initialize();
  }

  @Override
  protected MongoDBDriverTestAdapter getAdapter(String collectionName) {
    return new MongoDbReactiveDriverTestAdapterImpl(getDataBase().getCollection(collectionName));
  }
}
