package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.utils.IndependentDbIntegrationTestBase;
import com.github.cloudyrock.mongock.utils.SharedDbIntegrationTestBase;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public class LockRepositoryITest extends IndependentDbIntegrationTestBase {

  private static final String LOCK_COLLECTION_NAME = "mongocklock";
  private static final String LOCK_KEY = "LOCK_KEY";

  private LockRepository repository;


  @Before
  public void setUp() {
    repository = new LockRepository(LOCK_COLLECTION_NAME, db);
    repository.ensureIndex();
  }

  @Test
  public void ensureKeyUniqueness() {
    //inserting lock with key1: fine
    db.getCollection(LOCK_COLLECTION_NAME)
        .insertOne(new LockEntry("KEY1", "STATUS1", "process1", new Date(System.currentTimeMillis() - 60000))
            .buildFullDBObject());
    //inserting lock with key2: fine
    db.getCollection(LOCK_COLLECTION_NAME)
        .insertOne(new LockEntry("KEY2", "STATUS1", "process1", new Date(System.currentTimeMillis() - 60000))
            .buildFullDBObject());

    try {
      //inserting lock with key1 again: Exception
      db.getCollection(LOCK_COLLECTION_NAME)
          .insertOne(new LockEntry("KEY1", "STATUS2", "process2", new Date(System.currentTimeMillis() - 60000))
              .buildFullDBObject());

    } catch (MongoWriteException ex) {
      assertEquals(ErrorCategory.DUPLICATE_KEY, ex.getError().getCategory());
    }
  }

  @Test
  public void findByKeyShouldReturnLockWhenThereIsOne() throws LockPersistenceException, MongockException {
    //given
    db.getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set",
            new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1",
                new Date(System.currentTimeMillis() - 60000))
                .buildFullDBObject()),
        new UpdateOptions().upsert(true));

    //when
    final LockEntry result = repository.findByKey(LOCK_KEY);

    //then
    assertNotNull(result);
  }

  @Test
  public void insertUpdateShouldInsertWhenEmpty() throws LockPersistenceException, MongockException {

    // when
    Date expiresAtExpected = new Date(System.currentTimeMillis() - 60000);
    repository.insertUpdate(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1", expiresAtExpected));

    //then
    FindIterable<Document> result = db.getCollection(LOCK_COLLECTION_NAME).find(new Document().append("key", LOCK_KEY));
    assertNotNull(result.first());
    assertEquals(expiresAtExpected, result.first().get("expiresAt"));
  }

  @Test
  public void insertUpdateShouldUpdateWhenExpiresAtIsGraterThanSaved() throws LockPersistenceException, MongockException {
    //given
    final long currentMillis = System.currentTimeMillis();
    repository
        .insertUpdate(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1", new Date(currentMillis - 1000)));

    //when
    Date expiresAtExpected = new Date();
    repository.insertUpdate(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process2", expiresAtExpected));

    //then
    FindIterable<Document> result = db.getCollection(LOCK_COLLECTION_NAME).find(new Document().append("key", LOCK_KEY));
    assertNotNull(result.first());
    assertEquals(expiresAtExpected, result.first().get("expiresAt"));

  }

  @Test
  public void insertUpdateShouldUpdateWhenSameOwner() throws LockPersistenceException, MongockException {
    //given
    repository.insertUpdate(
        new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1",
            new Date(System.currentTimeMillis() + 60 * 60 * 1000)));

    //when
    Date expiresAtExpected = new Date();
    repository.insertUpdate(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1", expiresAtExpected));

    //then
    FindIterable<Document> result = db.getCollection(LOCK_COLLECTION_NAME).find(new Document().append("key", LOCK_KEY));
    assertNotNull(result.first());
    assertEquals(expiresAtExpected, result.first().get("expiresAt"));
  }

  @Test(expected = LockPersistenceException.class)
  public void insertUpdateShouldThrowExceptionWhenLockIsInDBWIthDifferentOwnerAndNotExpired() throws LockPersistenceException, MongockException {
    //given
    final long currentMillis = System.currentTimeMillis();
    repository
        .insertUpdate(
            new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1", new Date(currentMillis + 60 * 60 * 1000)));

    //when
    repository
        .insertUpdate(
            new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process2", new Date(currentMillis + 90 * 60 * 1000)));
  }

  @Test
  public void removeShouldRemoveWhenSameOwner() throws LockPersistenceException, MongockException {
    //given
    db.getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set",
            new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1",
                new Date(System.currentTimeMillis() - 600000))
                .buildFullDBObject()),
        new UpdateOptions().upsert(true));
    assertNotNull("Precondition: Lock should be in db",
        db.getCollection(LOCK_COLLECTION_NAME).find(new Document().append("key", LOCK_KEY)).first());

    //when
    repository.removeByKeyAndOwner(LOCK_KEY, "process1");

    //then
    assertNull(db.getCollection(LOCK_COLLECTION_NAME).find(new Document().append("key", LOCK_KEY)).first());
  }

  @Test
  public void removeShouldNotRemoveWhenDifferentOwner() throws LockPersistenceException, MongockException {
    //given
    db.getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set",
            new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1",
                new Date(System.currentTimeMillis() - 600000))
                .buildFullDBObject()),
        new UpdateOptions().upsert(true));
    assertNotNull("Precondition: Lock should be in db",
        db.getCollection(LOCK_COLLECTION_NAME).find(new Document().append("key", LOCK_KEY)).first());

    //when
    repository.removeByKeyAndOwner(LOCK_KEY, "process2");

    //then
    assertNotNull(db.getCollection(LOCK_COLLECTION_NAME).find(new Document().append("key", LOCK_KEY)).first());
  }

  @Test(expected = LockPersistenceException.class)
  public void updateIfSameOwnerShouldNotInsertWhenEmpty() throws LockPersistenceException, MongockException {
    //when
    repository.updateIfSameOwner(
        new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1",
            new Date(System.currentTimeMillis() - 600000)));
  }

  @Test(expected = LockPersistenceException.class)
  public void updateIfSameOwnerShouldNotUpdateWhenExpiresAtIsGraterThanSavedButOtherOwner() throws LockPersistenceException, MongockException {
    //given
    final long currentMillis = System.currentTimeMillis();
    repository
        .insertUpdate(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1", new Date(currentMillis - 1000)));

    //when
    repository
        .updateIfSameOwner(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process2", new Date(currentMillis)));

  }

  @Test
  public void updateIfSameOwnerShouldUpdateWhenSameOwner() throws LockPersistenceException, MongockException {
    //given
    repository.insertUpdate(
        new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1",
            new Date(System.currentTimeMillis() + 60 * 60 * 1000)));

    //when
    Date expiresAtExpected = new Date(System.currentTimeMillis());
    repository.updateIfSameOwner(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1", expiresAtExpected));

    //then
    FindIterable<Document> result = db.getCollection(LOCK_COLLECTION_NAME).find(new Document().append("key", LOCK_KEY));
    assertNotNull(result.first());
    assertEquals(expiresAtExpected, result.first().get("expiresAt"));
  }

  @Test(expected = LockPersistenceException.class)
  public void updateIfSameOwnerShouldNotUpdateWhenDifferentOwnerAndExpiresAtIsNotGrater() throws LockPersistenceException, MongockException {
    // given
    final long currentMillis = System.currentTimeMillis();
    repository
        .insertUpdate(
            new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process1", new Date(currentMillis + 60 * 60 * 1000)));

    // when
    repository.insertUpdate(new LockEntry(LOCK_KEY, LockStatus.LOCK_HELD.name(), "process2", new Date(currentMillis)));
  }

}
