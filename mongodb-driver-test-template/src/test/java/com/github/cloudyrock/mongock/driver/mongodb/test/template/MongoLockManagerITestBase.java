package com.github.cloudyrock.mongock.driver.mongodb.test.template;

import com.github.cloudyrock.mongock.driver.api.lock.LockCheckException;
import com.github.cloudyrock.mongock.driver.api.lock.LockManager;
import com.github.cloudyrock.mongock.driver.core.lock.DefaultLockManager;
import com.github.cloudyrock.mongock.driver.core.lock.LockEntry;
import com.github.cloudyrock.mongock.driver.core.lock.LockRepository;
import com.github.cloudyrock.mongock.driver.core.lock.LockStatus;
import com.github.cloudyrock.mongock.driver.mongodb.test.template.util.IntegrationTestBase;
import com.github.cloudyrock.mongock.utils.TimeService;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public abstract class MongoLockManagerITestBase extends IntegrationTestBase {
  protected static final String LOCK_COLLECTION_NAME = "changockLock";
  protected static final long lockActiveMillis = 5 * 60 * 1000;
  protected static final long maxWaitMillis = 5 * 60 * 1000;
  protected static final int lockMaxTries = 3;

  protected LockManager lockManager;
  protected LockRepository<Document> repository;

  @Before
  public void setUp() {
    initializeRepository();
    TimeService timeUtils = new TimeService();
    lockManager = new DefaultLockManager(repository, timeUtils)
        .setLockAcquiredForMillis(lockActiveMillis)
        .setLockMaxTries(lockMaxTries)
        .setLockMaxWaitMillis(maxWaitMillis);
  }

  @After
  public void tearDown() {
    getDataBase().getCollection(LOCK_COLLECTION_NAME).deleteMany(new Document());
  }


  @Test
  public void shouldAcquireLock_WhenHeld_IfSameOwner() throws LockCheckException {
    //given
    getDataBase().getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set", getLockDbBody(lockManager.getOwner(), currentTimePlusHours(24))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = getDataBase().getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", lockManager.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    lockManager.acquireLockDefault();
  }

  @Test
  public void shouldAcquireLock_WhenHeldByOtherOwner_IfExpired() throws LockCheckException {
    //given
    getDataBase().getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set", getLockDbBody("otherOwner", currentTimePlusHours(-1))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = getDataBase().getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", lockManager.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    lockManager.acquireLockDefault();
  }

  @Test
  public void shouldAcquireLock_WhenHeldByOtherOwner_IfExpiresAtIsLessThanMaxWaitTime() throws LockCheckException {
    //given
    getDataBase().getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set", getLockDbBody("otherOwner", System.currentTimeMillis() + 100)),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = getDataBase().getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", lockManager.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    lockManager.acquireLockDefault();
  }

  @Test(expected = LockCheckException.class)
  public void shouldNotAcquireLock_WhenHeldByOtherOwner_IfExpiresAtIsGreaterThanMaxWaitTime() throws LockCheckException {
    //given
    getDataBase().getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document()
            .append("$set", getLockDbBody("otherOwner", currentTimePlusMinutes(millisToMinutes(maxWaitMillis) + 1))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = getDataBase().getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", lockManager.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    lockManager.acquireLockDefault();
  }

  @Test(expected = LockCheckException.class)
  public void shouldNotEnsure_WhenFirstTime() throws LockCheckException {
    //when
    lockManager.ensureLockDefault();
  }

  /**
   * If it's not expired, the lock is ensured because still belongs to the owner
   */
  @Test
  public void shouldEnsureLock_WhenHeldBySameOwner_IfNotExpiredInDB() throws LockCheckException {
    //given
    getDataBase().getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set", getLockDbBody(lockManager.getOwner(), currentTimePlusMinutes(1))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = getDataBase().getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", lockManager.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    lockManager.ensureLockDefault();
  }

  /**
   * If it's  expired, the lock should be ensured because no one has requested, so it should be extended for the same
   * owner
   */
  @Test
  public void shouldEnsureLock_WhenHeldBySameOwner_IfExpiredInDB() throws LockCheckException {
    //given
    getDataBase().getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set", getLockDbBody(lockManager.getOwner(), currentTimePlusMinutes(-10))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = getDataBase().getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", lockManager.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    lockManager.ensureLockDefault();
  }

  @Test
  public void shouldEnsureLock_WhenAcquiredPreviously_IfSameOwner() throws LockCheckException {
    //given
    lockManager.acquireLockDefault();

    //when
    lockManager.ensureLockDefault();
  }


  @Test(expected = LockCheckException.class)
  public void shouldNotEnsureLock_WhenHeldByOtherOwnerAndExpiredInDB_ifHasNotBeenRequestedPreviously() throws LockCheckException {
    //given
    getDataBase().getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set", getLockDbBody("other", currentTimePlusMinutes(-10))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = getDataBase().getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", lockManager.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    lockManager.ensureLockDefault();
  }

  @Test(expected = LockCheckException.class)
  public void shouldNotEnsureLock_WhenHeldByOtherOwner_IfNotExpiredInDB() throws LockCheckException {
    //given
    getDataBase().getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set", getLockDbBody("other", currentTimePlusMinutes(10))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = getDataBase().getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", lockManager.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    lockManager.ensureLockDefault();
  }

  @Test
  public void shouldReleaseLock_WhenHeldBySameOwner() {
    //given
    getDataBase().getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set", getLockDbBody(lockManager.getOwner(), currentTimePlusMinutes(10))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = getDataBase().getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", lockManager.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    lockManager.releaseLockDefault();

    //then
    FindIterable<Document> resultAfter = getDataBase().getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", lockManager.getDefaultKey()));
    assertNull("Lock should be removed from DB", resultAfter.first());
  }

  @Test
  public void shouldNotReleaseLock_IfHeldByOtherOwner() {
    //given
    getDataBase().getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set", getLockDbBody("otherOwner", currentTimePlusMinutes(10))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = getDataBase().getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", lockManager.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    lockManager.releaseLockDefault();

    //then
    FindIterable<Document> resultAfter = getDataBase().getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", lockManager.getDefaultKey()));
    assertNotNull("Lock should be removed from DB", resultAfter.first());
  }

  @Test
  public void releaseLockShouldBeIdempotent_WhenHeldBySameOwner() {
    //given
    getDataBase().getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set", getLockDbBody(lockManager.getOwner(), currentTimePlusMinutes(10))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = getDataBase().getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", lockManager.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    lockManager.releaseLockDefault();
    lockManager.releaseLockDefault();

    //then
    FindIterable<Document> resultAfter = getDataBase().getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", lockManager.getDefaultKey()));
    assertNull("Lock should be removed from DB", resultAfter.first());
  }

  @Test
  public void releaseLockShouldBeIdempotent_WhenHeldByOtherOwner() {
    //given
    getDataBase().getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document().append("$set", getLockDbBody("otherOwner", currentTimePlusMinutes(10))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = getDataBase().getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", lockManager.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    lockManager.releaseLockDefault();
    lockManager.releaseLockDefault();

    //then
    FindIterable<Document> resultAfter = getDataBase().getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", lockManager.getDefaultKey()));
    assertNotNull("Lock should be removed from DB", resultAfter.first());
  }

  @Test
  public void releaseLockShouldNotThrowAnyException_WhenNoLockPresent() {
    //given
    FindIterable<Document> resultBefore = getDataBase().getCollection(LOCK_COLLECTION_NAME).find();
    assertNull("Precondition: Lock should not be in database", resultBefore.first());

    //when
    lockManager.releaseLockDefault();
    lockManager.releaseLockDefault();

    //then
    FindIterable<Document> resultAfter = getDataBase().getCollection(LOCK_COLLECTION_NAME).find();
    assertNull("Lock should be removed from DB", resultAfter.first());
  }

  private Document getLockDbBody(String owner, long expiresAt) {
    LockEntry lockEntry = new LockEntry(lockManager.getDefaultKey(), LockStatus.LOCK_HELD.name(), owner, new Date(expiresAt));
    return repository.toEntity(lockEntry);
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



  protected abstract void initializeRepository();
}
