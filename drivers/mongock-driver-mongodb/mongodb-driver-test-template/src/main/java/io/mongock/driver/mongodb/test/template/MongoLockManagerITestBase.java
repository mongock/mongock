package io.mongock.driver.mongodb.test.template;

import io.mongock.driver.api.lock.LockCheckException;
import io.mongock.driver.api.lock.LockManager;
import io.mongock.driver.core.lock.DefaultLockManager;
import io.mongock.driver.core.lock.LockEntry;
import io.mongock.driver.core.lock.LockRepositoryWithEntity;
import io.mongock.driver.core.lock.LockStatus;
import io.mongock.driver.mongodb.test.template.util.IntegrationTestBase;
import io.mongock.utils.TimeService;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public abstract class MongoLockManagerITestBase extends IntegrationTestBase {
  protected static final String LOCK_COLLECTION_NAME = "mongockLock";
  protected static final long LOCK_ACQUIRED_FOR_MILLIS = 5 * 60 * 1000;
  protected static final long LOCK_QUIT_TRYING_AFTER_MILLIS = 3 * LOCK_ACQUIRED_FOR_MILLIS;
  protected static final long LOCK_TRY_FRQUENCY_MILLIS = 1000L;



  protected LockManager lockManager;
  protected LockRepositoryWithEntity<Document> repository;

  @Before
  public void setUp() {
    initializeRepository();
    TimeService timeUtils = new TimeService();
    lockManager = DefaultLockManager.builder()
        .setLockRepository(repository)
        .setTimeUtils( timeUtils)
        .setLockAcquiredForMillis(LOCK_ACQUIRED_FOR_MILLIS)
        .setLockQuitTryingAfterMillis(LOCK_TRY_FRQUENCY_MILLIS)
        .setLockTryFrequencyMillis(LOCK_QUIT_TRYING_AFTER_MILLIS)
        .build();
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

//  @Test(expected = LockCheckException.class)
//  public void shouldNotAcquireLock_WhenHeldByOtherOwner_IfExpiresAtIsGreaterThanMaxWaitTime() throws LockCheckException {
//    //given
//    getDataBase().getCollection(LOCK_COLLECTION_NAME).updateMany(
//        new Document(),
//        new Document()
//            .append("$set", getLockDbBody("otherOwner", currentTimePlusMinutes(millisToMinutes(LOCK_TRY_FRQUENCY_MILLIS) + 1))),
//        new UpdateOptions().upsert(true));
//    FindIterable<Document> resultBefore = getDataBase().getCollection(LOCK_COLLECTION_NAME)
//        .find(new Document().append("key", lockManager.getDefaultKey()));
//    assertNotNull("Precondition: Lock should be in database", resultBefore.first());
//
//    //when
//    lockManager.acquireLockDefault();
//  }

//  @Test(expected = LockCheckException.class)
//  public void shouldNotEnsure_WhenFirstTime() throws LockCheckException {
//    //when
//    lockManager.ensureLockDefault();
//  }

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
    lockManager.acquireLockDefault();
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
    lockManager.acquireLockDefault();
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

//  private int millisToMinutes(long millis) {
//    return (int) (millis / (1000 * 60));
//  }



  protected abstract void initializeRepository();
}
