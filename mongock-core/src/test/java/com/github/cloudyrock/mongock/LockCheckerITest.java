package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.utils.IndependentDbIntegrationTestBase;
import com.mongodb.client.FindIterable;
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
  private static final String LOCK_COLLECTION_NAME = "mongocklock";
  private static final long lockActiveMillis = 5 * 60 * 1000;
  private static final long maxWaitMillis = 5 * 60 * 1000;
  private static final int lockMaxTries = 3;

  private LockChecker checker;

  @Before
  public void setUp() {
    LockRepository repository = new LockMongoRepository(LOCK_COLLECTION_NAME, db);
    repository.initialize();
    TimeUtils timeUtils = new TimeUtils();
    checker = new LockChecker(repository, timeUtils)
        .setLockAcquiredForMillis(lockActiveMillis)
        .setLockMaxTries(lockMaxTries)
        .setLockMaxWaitMillis(maxWaitMillis);
  }

  @Test
  public void shouldAcquireLock_WhenFirstTime() throws LockCheckException {
    checker.acquireLockDefault();
  }

  @Test
  public void shouldAcquireLock_WhenHeld_IfSameOwner() throws LockCheckException {
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
  public void shouldAcquireLock_WhenHeldByOtherOwner_IfExpired() throws LockCheckException {
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
  public void shouldAcquireLock_WhenHeldByOtherOwner_IfExpiresAtIsLessThanMaxWaitTime() throws LockCheckException {
    //given
    db.getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set", getLockDbBody("otherOwner", System.currentTimeMillis() + 100)),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = db.getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", LockChecker.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    checker.acquireLockDefault();
  }

  @Test(expected = LockCheckException.class)
  public void shouldNotAcquireLock_WhenHeldByOtherOwner_IfExpiresAtIsGreaterThanMaxWaitTime() throws LockCheckException {
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
  public void shouldNotEnsure_WhenFirstTime() throws LockCheckException {
    //when
    checker.ensureLockDefault();
  }

  /**
   * If it's not expired, the lock is ensured because still belongs to the owner
   */
  @Test
  public void shouldEnsureLock_WhenHeldBySameOwner_IfNotExpiredInDB() throws LockCheckException {
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

  /**
   * If it's  expired, the lock should be ensured because no one has requested, so it should be extended for the same
   * owner
   */
  @Test
  public void shouldEnsureLock_WhenHeldBySameOwner_IfExpiredInDB() throws LockCheckException {
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
  public void shouldEnsureLock_WhenAcquiredPreviously_IfSameOwner() throws LockCheckException {
    //given
    checker.acquireLockDefault();

    //when
    checker.ensureLockDefault();
  }


  @Test(expected = LockCheckException.class)
  public void shouldNotEnsureLock_WhenHeldByOtherOwnerAndExpiredInDB_ifHasNotBeenRequestedPreviously() throws LockCheckException {
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
  public void shouldNotEnsureLock_WhenHeldByOtherOwner_IfNotExpiredInDB() throws LockCheckException {
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
  public void shouldReleaseLock_WhenHeldBySameOwner() {
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
  public void shouldNotReleaseLock_IfHeldByOtherOwner() {
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
  public void releaseLockShouldBeIdempotent_WhenHeldBySameOwner() {
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
  public void releaseLockShouldBeIdempotent_WhenHeldByOtherOwner() {
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
  public void releaseLockShouldNotThrowAnyException_WhenNoLockPresent() {
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
