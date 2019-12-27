package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.utils.IndependentDbIntegrationTestBase;
import com.github.fakemongo.Fongo;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 *
 * @since 04/04/2018
 */
public class LockCheckerITest extends IndependentDbIntegrationTestBase {
  private static final String TEST_SERVER = "testServer";
  private static final String DB_NAME = "mongocktest";
  private static final String LOCK_COLLECTION_NAME = "mongocklock";
  private static final long lockActiveMillis = 5 * 60 * 1000;
  private static final long maxWaitMillis = 5 * 60 * 1000;
  private static final int lockMaxTries = 3;

  private LockChecker checker;

  @Before
  public void setUp() {
    LockRepository repository = new LockRepository(LOCK_COLLECTION_NAME, db);
    repository.ensureIndex();
    TimeUtils timeUtils = new TimeUtils();
    checker = new LockChecker(repository, timeUtils)
        .setLockAcquiredForMillis(lockActiveMillis)
        .setLockMaxTries(lockMaxTries)
        .setLockMaxWaitMillis(maxWaitMillis);
  }

  @Test
  public void shouldAcquireLockWhenFirstTime() throws LockCheckException {
    checker.acquireLockDefault();
  }

  @Test
  public void shouldAcquireLockWhenLockHeldBySameOwner() throws LockCheckException {
    //given
    db.getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set", getLockDbBody(checker.getOwner(), currentTimePlusHours(24))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = db.getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", LockChecker.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    checker.acquireLockDefault();
  }

  @Test
  public void shouldAcquireLockWhenLockHeldByOtherAndExpired() throws LockCheckException {
    //given
    db.getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set", getLockDbBody("otherOwner", currentTimePlusHours(-1))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = db.getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", LockChecker.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    checker.acquireLockDefault();
  }

  @Test
  public void shouldAcquireLockWhenLockHeldByOtherAndExpiresAtLtMaxWaitTime() throws LockCheckException {
    //given
    db.getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document()
            .append("$set", getLockDbBody("otherOwner", System.currentTimeMillis() + 100)),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = db.getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", LockChecker.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    checker.acquireLockDefault();
  }

  @Test(expected = LockCheckException.class)
  public void shouldNotAcquireLockWhenLockHeldByOtherAndExpiresAtGtMaxWaitTime() throws LockCheckException {
    //given
    db.getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document()
            .append("$set", getLockDbBody("otherOwner", currentTimePlusMinutes(millisToMinutes(maxWaitMillis) + 1))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = db.getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", LockChecker.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    checker.acquireLockDefault();
  }

  @Test(expected = LockCheckException.class)
  public void shouldNotEnsureWhenFirstTime() throws LockCheckException {
    //when
    checker.ensureLockDefault();
  }

  @Test
  public void shouldEnsureWhenHeldBySameOwnerAndNotExpiredInDB() throws LockCheckException {
    //given
    db.getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set", getLockDbBody(checker.getOwner(), currentTimePlusMinutes(1))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = db.getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", LockChecker.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    checker.ensureLockDefault();
  }

  @Test
  public void shouldEnsureWhenHeldBySameOwnerAndExpiredInDB() throws LockCheckException {
    //given
    db.getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set", getLockDbBody(checker.getOwner(), currentTimePlusMinutes(-10))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = db.getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", LockChecker.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    checker.ensureLockDefault();
  }

  @Test
  public void shouldEnsureWhenAcquiredPreviouslyBySameOwner() throws LockCheckException {
    //given
    checker.acquireLockDefault();

    //when
    checker.ensureLockDefault();
  }

  @Test(expected = LockCheckException.class)
  public void shouldNotEnsureWhenHeldByOtherOwnerAndExpiredInDB() throws LockCheckException {
    //given
    db.getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set", getLockDbBody("other", currentTimePlusMinutes(-10))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = db.getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", LockChecker.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    checker.ensureLockDefault();
  }

  @Test(expected = LockCheckException.class)
  public void shouldNotEnsureWhenHeldByOtherOwnerAndNotExpiredInDB() throws LockCheckException {
    //given
    db.getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set", getLockDbBody("other", currentTimePlusMinutes(10))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = db.getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", LockChecker.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    checker.ensureLockDefault();
  }

  @Test
  public void shouldReleaseLockWhenHeldBySameOwner() {
    //given
    db.getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set", getLockDbBody(checker.getOwner(), currentTimePlusMinutes(10))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = db.getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", LockChecker.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    checker.releaseLockDefault();

    //then
    FindIterable<Document> resultAfter = db.getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", LockChecker.getDefaultKey()));
    assertNull("Lock should be removed from DB", resultAfter.first());
  }

  @Test
  public void shouldNotReleaseLockWhenHeldByOtherOwner() {
    //given
    db.getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set", getLockDbBody("otherOwner", currentTimePlusMinutes(10))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = db.getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", LockChecker.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    checker.releaseLockDefault();

    //then
    FindIterable<Document> resultAfter = db.getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", LockChecker.getDefaultKey()));
    assertNotNull("Lock should be removed from DB", resultAfter.first());
  }

  @Test
  public void releaseLockShouldBeIdempotentWhenHeldBySameOwner() {
    //given
    db.getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set", getLockDbBody(checker.getOwner(), currentTimePlusMinutes(10))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = db.getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", LockChecker.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    checker.releaseLockDefault();
    checker.releaseLockDefault();

    //then
    FindIterable<Document> resultAfter = db.getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", LockChecker.getDefaultKey()));
    assertNull("Lock should be removed from DB", resultAfter.first());
  }

  @Test
  public void releaseLockShouldBeIdempotentWhenHeldByOtherOwner() {
    //given
    db.getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set", getLockDbBody("otherOwner", currentTimePlusMinutes(10))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = db.getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", LockChecker.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    checker.releaseLockDefault();
    checker.releaseLockDefault();

    //then
    FindIterable<Document> resultAfter = db.getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", LockChecker.getDefaultKey()));
    assertNotNull("Lock should be removed from DB", resultAfter.first());
  }

  @Test
  public void releaseLockShouldNotThrowAnyExceptionWhenNoLockPresent() {
    //given
    FindIterable<Document> resultBefore = db.getCollection(LOCK_COLLECTION_NAME).find();
    assertNull("Precondition: Lock should not be in database", resultBefore.first());

    //when
    checker.releaseLockDefault();
    checker.releaseLockDefault();

    //then
    FindIterable<Document> resultAfter = db.getCollection(LOCK_COLLECTION_NAME).find();
    assertNull("Lock should be removed from DB", resultAfter.first());
  }

  private Document getLockDbBody(String owner, long expiresAt) {
    return new LockEntry(
        LockChecker.getDefaultKey(),
        LockStatus.LOCK_HELD.name(),
        owner,
        new Date(expiresAt)
    ).buildFullDBObject();
  }

  private long currentTimePlusHours(int hours) {
    return currentTimePlusMinutes(hours * 60);
  }

  private long currentTimePlusMinutes(int minutes) {
    long millis = minutes * 60 * 1000;
    return System.currentTimeMillis() + millis;
  }

  private int millisToMinutes(long millis) {
    return (int) (millis / (1000 * 60));
  }
}
