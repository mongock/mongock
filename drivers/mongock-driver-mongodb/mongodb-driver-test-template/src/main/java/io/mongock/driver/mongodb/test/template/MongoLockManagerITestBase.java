package io.mongock.driver.mongodb.test.template;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.UpdateOptions;
import io.mongock.driver.api.lock.LockCheckException;
import io.mongock.driver.api.lock.LockManager;
import io.mongock.driver.core.lock.DaemonLockManager;
import io.mongock.driver.core.lock.LockEntry;
import io.mongock.driver.core.lock.LockRepositoryWithEntity;
import io.mongock.driver.core.lock.LockStatus;
import io.mongock.driver.mongodb.test.template.util.IntegrationTestBase;
import io.mongock.utils.TimeService;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
  }

  private void getLockManager(TimeService timeService,
                              long acquireForMillis,
                              long tryFreq,
                              long quickTrying) {
    lockManager = DaemonLockManager.builder()
        .setLockRepository(repository)
        .setTimeService(timeService)
        .setLockAcquiredForMillis(acquireForMillis)
        .setLockTryFrequencyMillis(tryFreq)
        .setLockQuitTryingAfterMillis(quickTrying)
        .build();
  }

  @After
  public void tearDown() {
    getDataBase().getCollection(LOCK_COLLECTION_NAME).deleteMany(new Document());
  }


  @Test
  public void shouldAcquireLock_WhenHeld_IfSameOwner() throws LockCheckException {
    //given
    getLockManager(new TimeService(), LOCK_ACQUIRED_FOR_MILLIS, LOCK_TRY_FRQUENCY_MILLIS, LOCK_QUIT_TRYING_AFTER_MILLIS);
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
  public void shouldAcquireLock_WhenHeldByOther_IfExpired() throws LockCheckException {
    //given
    getLockManager(new TimeService(), LOCK_ACQUIRED_FOR_MILLIS, LOCK_TRY_FRQUENCY_MILLIS, LOCK_QUIT_TRYING_AFTER_MILLIS);
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

  /*
  The lock is held by other. When the current tries to take it's still held, but it will keep trying for a while and
  it eventually get it as the expires time of the lock currently in db is shorter than the time the process trying to
  acquire the lock will be re-trying.
   */
  @Test
  public void shouldFinallyAcquireLock_WhenTheLockIsHeldByOther_IfTheTimeItWIllBeReTryingIsLongerThanTheCurrentLockExpireTime() throws LockCheckException {
    //given
    getLockManager(new TimeService(), LOCK_ACQUIRED_FOR_MILLIS, LOCK_TRY_FRQUENCY_MILLIS, LOCK_QUIT_TRYING_AFTER_MILLIS);
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

  /*
  The lock is held by other. When the current tries to take it's still held, but it will keep trying for a while. However
   the time it will keep trying is not long enough and it will finally fail. So the lock will kept by the current owner
 */
  @Test(expected = LockCheckException.class)
  public void shouldNotAcquireLock_WhenHeldByOther_IfExpiresAtIsGreaterThanMaxWaitTime() throws LockCheckException {
    //given
    long acquireFor = 3000L;
    getLockManager(new TimeService(), acquireFor, LOCK_TRY_FRQUENCY_MILLIS, 1000L);
    getDataBase().getCollection(LOCK_COLLECTION_NAME).updateMany(
        new Document(),
        new Document()
            .append("$set", getLockDbBody("otherOwner", currentTimePlusMinutes(1))),
        new UpdateOptions().upsert(true));
    FindIterable<Document> resultBefore = getDataBase().getCollection(LOCK_COLLECTION_NAME)
        .find(new Document().append("key", lockManager.getDefaultKey()));
    assertNotNull("Precondition: Lock should be in database", resultBefore.first());

    //when
    lockManager.acquireLockDefault();
  }

  @Test(expected = LockCheckException.class)
  public void shouldThrowException_WhenEnsuring_IfNotAcquiredFirst() throws LockCheckException {
    //when

    getLockManager(new TimeService(), LOCK_ACQUIRED_FOR_MILLIS, LOCK_TRY_FRQUENCY_MILLIS, 1000L);

    lockManager.ensureLockDefault();
  }

  /**
   * If it's not expired, the lock is ensured because still belongs to the owner
   */
  @Test
  public void shouldEnsureLock_WhenHeldBySame_IfNotExpiredInDB() throws LockCheckException {
    //given
    getLockManager(new TimeService(), LOCK_ACQUIRED_FOR_MILLIS, LOCK_TRY_FRQUENCY_MILLIS, 1000L);

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
  public void shouldEnsureLock_WhenHeldBySame_IfExpiredInDB() throws LockCheckException {
    //given
    getLockManager(new TimeService(), LOCK_ACQUIRED_FOR_MILLIS, LOCK_TRY_FRQUENCY_MILLIS, 1000L);
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
    getLockManager(new TimeService(), LOCK_ACQUIRED_FOR_MILLIS, LOCK_TRY_FRQUENCY_MILLIS, 1000L);

    lockManager.acquireLockDefault();

    //when
    lockManager.ensureLockDefault();
  }


  @Test(expected = LockCheckException.class)
  public void shouldNotEnsureLock_WhenHeldByOtherAndExpiredInDB_ifHasNotBeenRequestedPreviously() throws LockCheckException {
    //given
    getLockManager(new TimeService(), LOCK_ACQUIRED_FOR_MILLIS, LOCK_TRY_FRQUENCY_MILLIS, 1000L);
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
  public void shouldNotEnsureLock_WhenHeldByOther_IfNotExpiredInDB() throws LockCheckException {
    //given
    getLockManager(new TimeService(), LOCK_ACQUIRED_FOR_MILLIS, LOCK_TRY_FRQUENCY_MILLIS, 1000L);
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
    getLockManager(new TimeService(), 3000L, LOCK_TRY_FRQUENCY_MILLIS, 1000L);
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
    getLockManager(new TimeService(), 3000L, LOCK_TRY_FRQUENCY_MILLIS, 1000L);
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
    getLockManager(new TimeService(), 3000L, 3000L, 1000L);

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
    getLockManager(new TimeService(), 3000L, LOCK_TRY_FRQUENCY_MILLIS, 1000L);
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
  public void releaseLockShouldNotThrowAnyException_WhenLockNoPresent() {
    //given
    getLockManager(new TimeService(), 3000L, LOCK_TRY_FRQUENCY_MILLIS, 1000L);

    FindIterable<Document> resultBefore = getDataBase().getCollection(LOCK_COLLECTION_NAME).find();
    assertNull("Precondition: Lock should not be in database", resultBefore.first());

    //when
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
