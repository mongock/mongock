package io.mongock.driver.mongodb.test.template;


import io.mongock.driver.core.lock.LockEntry;
import io.mongock.driver.core.lock.LockPersistenceException;
import io.mongock.driver.core.lock.LockRepositoryWithEntity;
import io.mongock.driver.core.lock.LockStatus;
import io.mongock.driver.mongodb.test.template.interfaces.MongoLockRepositoryITestInterface;
import io.mongock.driver.mongodb.test.template.util.IntegrationTestBase;
import io.mongock.api.exception.MongockException;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


public abstract class MongoLockRepositoryITestBase extends IntegrationTestBase implements MongoLockRepositoryITestInterface {

  private static final String LOCK_COLLECTION_NAME = "mongockLock";
  private static final String LOCK_KEY = "LOCK_KEY";

  protected LockRepositoryWithEntity<Document> repository;


  @Test
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
  public void findByKeyShouldReturnLockWhenThereIsOne() throws LockPersistenceException, MongockException {
    initializeRepository();
    //given
    getDataBase().getCollection(LOCK_COLLECTION_NAME).updateMany(
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
  public void insertUpdateShouldInsertWhenEmpty() throws LockPersistenceException, MongockException {
    initializeRepository();

    // when
    Date expiresAtExpected = new Date(System.currentTimeMillis() - 60000);
    repository.insertUpdate(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1", expiresAtExpected));

    //then
    FindIterable<Document> result = getDataBase().getCollection(LOCK_COLLECTION_NAME).find(new Document().append("key", LOCK_KEY));
    assertNotNull(result.first());
    assertEquals(expiresAtExpected, result.first().get("expiresAt"));
  }

  @Test
  public void insertUpdateShouldUpdateWhenExpiresAtIsGraterThanSaved() throws LockPersistenceException, MongockException {
    initializeRepository();
    //given
    final long currentMillis = System.currentTimeMillis();
    repository
        .insertUpdate(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1", new Date(currentMillis - 1000)));

    //when
    Date expiresAtExpected = new Date();
    repository.insertUpdate(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process2", expiresAtExpected));

    //then
    FindIterable<Document> result = getDataBase().getCollection(LOCK_COLLECTION_NAME).find(new Document().append("key", LOCK_KEY));
    assertNotNull(result.first());
    assertEquals(expiresAtExpected, result.first().get("expiresAt"));

  }

  @Test
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
    FindIterable<Document> result = getDataBase().getCollection(LOCK_COLLECTION_NAME).find(new Document().append("key", LOCK_KEY));
    assertNotNull(result.first());
    assertEquals(expiresAtExpected, result.first().get("expiresAt"));
  }

  @Test
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
            new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process2", new Date(currentMillis + 90 * 60 * 1000)))
    );
  }

  @Test
  public void removeShouldRemoveWhenSameOwner() throws LockPersistenceException, MongockException {
    initializeRepository();
    //given
    getDataBase().getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set",
            repository.toEntity(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1",
                new Date(System.currentTimeMillis() - 600000)))),
        new UpdateOptions().upsert(true));
    assertNotNull(getDataBase().getCollection(LOCK_COLLECTION_NAME).find(new Document().append("key", LOCK_KEY)).first(),
            "Precondition: Lock should be in getDataBase()");

    //when
    repository.removeByKeyAndOwner(LOCK_KEY, "process1");

    //then
    assertNull(getDataBase().getCollection(LOCK_COLLECTION_NAME).find(new Document().append("key", LOCK_KEY)).first());
  }

  @Test
  public void removeShouldNotRemoveWhenDifferentOwner() throws LockPersistenceException, MongockException {
    initializeRepository();
    //given
    getDataBase().getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set",
            repository.toEntity(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1",
                new Date(System.currentTimeMillis() - 600000)))),
        new UpdateOptions().upsert(true));
    assertNotNull(getDataBase().getCollection(LOCK_COLLECTION_NAME).find(new Document().append("key", LOCK_KEY)).first(),
            "Precondition: Lock should be in getDataBase()");

    //when
    repository.removeByKeyAndOwner(LOCK_KEY, "process2");

    //then
    assertNotNull(getDataBase().getCollection(LOCK_COLLECTION_NAME).find(new Document().append("key", LOCK_KEY)).first());
  }

  @Test
  public void updateIfSameOwnerShouldNotInsertWhenEmpty() throws LockPersistenceException, MongockException {
    initializeRepository();
    //when
    assertThrows(LockPersistenceException.class, () -> repository.updateIfSameOwner(
        new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1",
            new Date(System.currentTimeMillis() - 600000)))
    );
  }

  @Test
  public void updateIfSameOwnerShouldNotUpdateWhenExpiresAtIsGraterThanSavedButOtherOwner() throws LockPersistenceException, MongockException {
    initializeRepository();
    //given
    final long currentMillis = System.currentTimeMillis();
    repository
        .insertUpdate(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1", new Date(currentMillis - 1000)));

    //when
    assertThrows(LockPersistenceException.class, () -> repository
        .updateIfSameOwner(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process2", new Date(currentMillis)))
    );
  }

  @Test
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
    FindIterable<Document> result = getDataBase().getCollection(LOCK_COLLECTION_NAME).find(new Document().append("key", LOCK_KEY));
    assertNotNull(result.first());
    assertEquals(expiresAtExpected, result.first().get("expiresAt"));
  }

  @Test
  public void updateIfSameOwnerShouldNotUpdateWhenDifferentOwnerAndExpiresAtIsNotGrater() throws LockPersistenceException, MongockException {
    initializeRepository();
    // given
    final long currentMillis = System.currentTimeMillis();
    repository
        .insertUpdate(
            new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1", new Date(currentMillis + 60 * 60 * 1000)));

    // when
    assertThrows(LockPersistenceException.class, () -> 
            repository.insertUpdate(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process2", new Date(currentMillis)))
    );
  }

}
