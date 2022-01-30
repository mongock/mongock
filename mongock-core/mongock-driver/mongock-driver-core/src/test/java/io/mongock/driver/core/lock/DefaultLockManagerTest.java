package io.mongock.driver.core.lock;

import io.mongock.driver.api.lock.LockCheckException;
import io.mongock.driver.api.lock.LockManager;
import io.mongock.utils.TimeService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.verification.AtLeast;
import org.mockito.internal.verification.AtMost;
import org.mockito.internal.verification.Times;

import java.time.Instant;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultLockManagerTest {

  private static final long lockActiveMillis = 5 * 60 * 1000L;
  private static final long quitTryingAfterMillis = 3 * 60 * 1000L;
  private static final long tryFrequency = 5 * 1000L;
  private static final int DONT_CARE_LONG = 1;
  private static final Instant DONT_CARE_INSTANT = Instant.now();
  private static final Date DONT_CARE_DATE = new Date(0L);
  private static final Date FAR_FUTURE_DATE = new Date(100000L);

  private LockRepositoryWithEntity lockRepository;
  private TimeService timeService;
  private LockManager lockManager;

  private static void assertExceptionMessage(LockCheckException ex) {
    assertTrue(ex.getMessage().contains("Quit trying lock after 180000 millis due to LockPersistenceException:"));
  }


  @Before
  public void setUp() {
    lockRepository = Mockito.mock(LockRepositoryWithEntity.class);
    timeService = Mockito.mock(TimeService.class);
    when(timeService.nowPlusMillis(anyLong())).thenReturn(DONT_CARE_INSTANT);
  }

  private void setLockManager(long lockActiveMillis, long quitTryingAfterMillis, long tryFrequency) {
    lockManager = DaemonLockManager.builder()
        .setLockRepository(lockRepository)
        .setTimeService(timeService)
        .setLockAcquiredForMillis(lockActiveMillis)
        .setLockQuitTryingAfterMillis(quitTryingAfterMillis)
        .setLockTryFrequencyMillis(tryFrequency)
        .build();
  }

  @After
  public void tearDown() {
    if (lockManager != null) {
      lockManager.close();
    }
  }

  @Test
  public void shouldRefresh_InBackground_IfNotExplicitCall() throws InterruptedException {
    when(timeService.currentTime()).thenReturn(new Date(40000L));// Exactly the expiration time(minus margin)

    DaemonLockManager lockManager = DaemonLockManager.builder()
        .setLockRepository(lockRepository)
        .setTimeService(timeService)
        .setLockAcquiredForMillis(3000L)
        .setLockQuitTryingAfterMillis(quitTryingAfterMillis)
        .setLockTryFrequencyMillis(tryFrequency)
        .setLockTryFrequencyMillis(1000L)
        .build();



    DaemonLockManager lockManagerSpy = Mockito.spy(lockManager);
    lockManagerSpy.acquireLockDefault();
    Thread.sleep(7000L);

    verify(lockManagerSpy, new AtLeast(1)).ensureLockDefault();
    verify(lockManagerSpy, new AtMost(5)).ensureLockDefault();
  }

  @Test
  public void shouldRefresh_InBackground_AfterManagerIsClosed() throws InterruptedException {
    when(timeService.currentTime()).thenReturn(new Date(40000L));// Exactly the expiration time(minus margin)


    DaemonLockManager lockManager = DaemonLockManager.builder()
        .setLockRepository(lockRepository)
        .setTimeService(timeService)
        .setLockAcquiredForMillis(3000L)
        .setLockQuitTryingAfterMillis(quitTryingAfterMillis)
        .setLockTryFrequencyMillis(tryFrequency)
        .setLockTryFrequencyMillis(2000L)
        .build();
    DaemonLockManager lockManagerSpy = Mockito.spy(lockManager);
    lockManagerSpy.acquireLockDefault();
    lockManagerSpy.close();
    Thread.sleep(7000L);

    verify(lockManagerSpy, new Times(0)).ensureLockDefault();
  }


  @Test
  public void shouldRetrieveLock_WhenAcquireLock() throws LockPersistenceException, LockCheckException {
    //given
    Date expirationAt = new Date(1000L);
    when(timeService.currentDatePlusMillis(anyLong())).thenReturn(expirationAt);
    setLockManager(lockActiveMillis, quitTryingAfterMillis, tryFrequency);

    // when
    lockManager.acquireLockDefault();

    //then
    assertInsertUpdateCall(expirationAt, 1);
  }

  @Test
  public void shouldAlwaysAskForLock_WhenAcquireLock_RegardlessIfItIsAlreadyAcquired() throws LockPersistenceException, LockCheckException {

    //given
    Date expirationAt = new Date(1000L);
    when(timeService.currentDatePlusMillis(anyLong())).thenReturn(expirationAt);
    when(timeService.currentTime()).thenReturn(new Date(40000L));// Exactly the expiration time(minus margin)
    when(timeService.nowPlusMillis(anyLong())).thenReturn(DONT_CARE_INSTANT);
    setLockManager(lockActiveMillis, quitTryingAfterMillis, tryFrequency);
    lockManager.acquireLockDefault();

    //when
    lockManager.acquireLockDefault();

    //then
    assertInsertUpdateCall(expirationAt, 2);
  }

  @Test
  public void shouldAlwaysAskForLock_WhenAcquireLock_RegardlessIfItIsExpired() throws LockPersistenceException, LockCheckException {
    //given
    Date expirationAt = new Date(1000L);
    when(timeService.currentDatePlusMillis(anyLong())).thenReturn(expirationAt);
    when(timeService.currentTime()).thenReturn(new Date(39999L));// 1ms less than the expiration time(minus margin)
    setLockManager(lockActiveMillis, quitTryingAfterMillis, tryFrequency);
    lockManager.acquireLockDefault();

    // when
    lockManager.acquireLockDefault();

    //then
    assertInsertUpdateCall(expirationAt, 2);
  }

  @Test
  public void shouldWaitMinimum_IfWaitingTimeIsLessThanMinimum_WhenAcquireLock() throws LockPersistenceException, LockCheckException {
    //given
    long expiresAt = 3000L;
    long waitingTime = 0L;
    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail"))
        .doNothing()
        .when(lockRepository).insertUpdate(any(LockEntry.class));

    Date newExpirationAt = new Date(1000L);
    when(timeService.currentDatePlusMillis(anyLong())).thenReturn(newExpirationAt);
    when(timeService.currentTime()).thenReturn(new Date(expiresAt - waitingTime));
    setLockManager(lockActiveMillis, quitTryingAfterMillis, tryFrequency);
    when(lockRepository.findByKey(anyString())).thenReturn(createFakeLockWithOtherOwner(expiresAt));

    //when
    long timeBeforeCall = System.currentTimeMillis();
    lockManager.acquireLockDefault();
    long timeSpent = System.currentTimeMillis() - timeBeforeCall;

    //then
    assertTrue("Checker should wait at least " + waitingTime + "ms", timeSpent >= waitingTime);
    assertInsertUpdateCall(newExpirationAt, 2);
  }

  @Test
  public void shouldWaitUntilExpiration_IfFrequencyIsHigherThanExpiration_WhenAcquireLock() throws LockPersistenceException, LockCheckException {
    //given

    long expiresAt = 3000L;
    long currentMoment = 2000L;
    long waitingTime = expiresAt - currentMoment;
    when(timeService.currentDatePlusMillis(anyLong())).thenReturn(DONT_CARE_DATE);
    when(timeService.currentTime()).thenReturn(new Date(currentMoment));

    //when
    long instantBefore = System.currentTimeMillis();
    setLockManager(lockActiveMillis, quitTryingAfterMillis, 3000L);
    when(lockRepository.findByKey(anyString())).thenReturn(createFakeLockWithOtherOwner(expiresAt));
    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail"))
        .doNothing()
        .when(lockRepository).insertUpdate(any(LockEntry.class));

    lockManager.acquireLockDefault();
    long spentMillis = System.currentTimeMillis() - instantBefore;

    //then
    assertTrue("Checker should wait at least " + waitingTime + "ms", spentMillis >= waitingTime);
    assertInsertUpdateCall(DONT_CARE_DATE, 2);
  }

  @Test
  public void shouldWaitFrequency_IfFrequencyIsLowerThanExpiration_WhenAcquireLock() throws LockPersistenceException, LockCheckException {
    //given
    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail"))
        .doNothing()
        .when(lockRepository).insertUpdate(any(LockEntry.class));

    long expiresAt = 3000L;
    long currentMoment = 2000L;
    long waitingTime = 750L;
    when(timeService.currentDatePlusMillis(anyLong())).thenReturn(DONT_CARE_DATE);
    when(timeService.currentTime()).thenReturn(new Date(currentMoment));

    //when
    long instantBefore = System.currentTimeMillis();
    setLockManager(lockActiveMillis, quitTryingAfterMillis, waitingTime);
    when(lockRepository.findByKey(anyString())).thenReturn(createFakeLockWithOtherOwner(expiresAt));
    lockManager.acquireLockDefault();
    long spentMillis = System.currentTimeMillis() - instantBefore;

    //then
    assertTrue("Checker should wait at least " + waitingTime + "ms", spentMillis >= waitingTime);
    assertInsertUpdateCall(DONT_CARE_DATE, 2);
  }

  @Test
  public void shouldNotWaitAndThrowException_IfWaitingTimeIsOver_WhenAcquireLock() throws LockPersistenceException, LockCheckException {
    //given

    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail"))
        .doNothing()
        .when(lockRepository).insertUpdate(any(LockEntry.class));

    when(timeService.currentDatePlusMillis(anyLong())).thenReturn(FAR_FUTURE_DATE);
    when(timeService.currentTime()).thenReturn(new Date(2000L));
    when(timeService.nowPlusMillis(anyLong())).thenReturn(DONT_CARE_INSTANT);
    when(timeService.isPast(any(Instant.class))).thenReturn(true);

    setLockManager(lockActiveMillis, 1000L, tryFrequency);
    when(lockRepository.findByKey(anyString())).thenReturn(createFakeLockWithOtherOwner(DONT_CARE_LONG));

    //when
    long timeBeforeCall = System.currentTimeMillis();
    try {
      lockManager.acquireLockDefault();
    } catch (LockCheckException ex) {
      long timeSpent = System.currentTimeMillis() - timeBeforeCall;
      assertTrue("Should abort straight away, a couple of millis", timeSpent <= 50L);
      assertInsertUpdateCall(FAR_FUTURE_DATE, 1);
      return;
    }
    fail();

    //then
  }

  @Test
  public void shouldNotWaiAndTryAgainStraightAway_IfWriteLockThrowsExceptionButItIsSameOwner_WhenAcquireLock() throws LockPersistenceException, LockCheckException {
    //given
    long expiresAt = 3000L;
    long waitingTime = 1000L;
    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail"))
        .doNothing()
        .when(lockRepository).insertUpdate(any(LockEntry.class));

    setLockManager(lockActiveMillis, quitTryingAfterMillis, waitingTime);
    when(lockRepository.findByKey(anyString())).thenReturn(new LockEntry(
        lockManager.getDefaultKey(),
        LockStatus.LOCK_HELD.name(),
        lockManager.getOwner(),
        new Date(expiresAt)
    ));
    Date newExpirationAt = new Date(100000L);
    when(timeService.currentDatePlusMillis(anyLong())).thenReturn(newExpirationAt);
    when(timeService.currentTime())
        .thenReturn(new Date(40000L))
        .thenReturn(new Date(expiresAt - waitingTime));

    //when
    long timeBeforeCall = System.currentTimeMillis();
    lockManager.acquireLockDefault();
    long timeSpent = System.currentTimeMillis() - timeBeforeCall;

    //then
    assertTrue("Checker should wait that long", timeSpent < waitingTime);
    assertInsertUpdateCall(newExpirationAt, 2);
  }

  @Test
  public void shouldThrowException_IfQuitTryingAfterReached_WhenAcquire()
      throws LockPersistenceException {
    //given
    long expiresAt = 3000L;
    long waitingTime = quitTryingAfterMillis + 1;
    int invocationTimes = 1;

    setLockManager(lockActiveMillis, quitTryingAfterMillis, waitingTime);
    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail")).when(lockRepository).insertUpdate(any(LockEntry.class));
    when(lockRepository.findByKey(anyString())).thenReturn(createFakeLockWithOtherOwner(expiresAt));
    Date newExpirationAt = new Date(100000L);
    when(timeService.currentDatePlusMillis(anyLong())).thenReturn(newExpirationAt);
    when(timeService.currentTime()).thenReturn(new Date(expiresAt - waitingTime));
    when(timeService.isPast(any(Instant.class))).thenReturn(true);

    //when
    long timeBeforeCall = System.currentTimeMillis();
    try {
      lockManager.acquireLockDefault();
    } catch (LockCheckException ex) {
      //then
      assertTrue((System.currentTimeMillis() - timeBeforeCall) < waitingTime);
      assertInsertUpdateCall(newExpirationAt, invocationTimes);
      assertExceptionMessage(ex);
      return;
    }
    fail();
  }

  //
  @Test
  public void shouldKeepTryingToAcquireLock_WhileQuitTryingAfterNotReached_WhenAcquireLock() throws LockPersistenceException {
    //given
    long expiresAt = 3000L;
    long waitingTime = 1;
    int invocationTimes = 3;

    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail")).when(lockRepository).insertUpdate(any(LockEntry.class));

    Date newExpirationAt = new Date(100000L);
    when(timeService.currentDatePlusMillis(anyLong())).thenReturn(newExpirationAt);
    when(timeService.currentTime()).thenReturn(new Date(expiresAt - waitingTime));
    when(timeService.nowPlusMillis(anyLong())).thenReturn(DONT_CARE_INSTANT);
    doReturn(false, false, true).when(timeService).isPast(any(Instant.class));
    setLockManager(lockActiveMillis, quitTryingAfterMillis, tryFrequency);
    when(lockRepository.findByKey(anyString())).thenReturn(createFakeLockWithOtherOwner(expiresAt));

    // when
    try {
      lockManager.acquireLockDefault();
    } catch (LockCheckException ex) {
      //then
      assertInsertUpdateCall(newExpirationAt, invocationTimes);
      assertExceptionMessage(ex);
      return;
    }
    fail();
  }

  @Test
  public void shouldCallRepository_WhenEnsureLock() throws LockPersistenceException, LockCheckException {
    //given
    Date expirationAt = new Date(1000L);
    when(timeService.currentDatePlusMillis(anyLong())).thenReturn(expirationAt);
    setLockManager(lockActiveMillis, quitTryingAfterMillis, tryFrequency);

    // when
    lockManager.ensureLockDefault();

    //then
    assertUpdateCallIfSameOwner(expirationAt, 1);
  }

  @Test
  public void shouldRefreshLock_IfLockIsExpired_whenEnsureLock() throws LockPersistenceException, LockCheckException {
    //given
    Date expirationAt = new Date(20 * 1000L);
    when(timeService.currentDatePlusMillis(anyLong())).thenReturn(expirationAt);
    when(timeService.currentTime()).thenReturn(new Date(40000L));// Exactly the expiration time(minus margin)

    setLockManager(3 * 1000L, quitTryingAfterMillis, tryFrequency);// 3 seconds. Margin should 1 second
    lockManager.acquireLockDefault();

    // when
    lockManager.ensureLockDefault();

    //then
    assertUpdateCallIfSameOwner(expirationAt, 1);
  }

  //
  @Test
  public void shouldNotRefreshLock_IfAlreadyAcquired_WhenEnsureLock() throws LockPersistenceException, LockCheckException {
    //given
    Date expirationAt = new Date(42 * 1000L);
    when(timeService.currentDatePlusMillis(anyLong())).thenReturn(expirationAt);
    when(timeService.currentTime()).thenReturn(new Date(39999L));// 1ms less than the expiration time(minus margin)

    setLockManager(3 * 1000L, quitTryingAfterMillis, tryFrequency);// 3 seconds. Margin should 1 second
    lockManager.acquireLockDefault();

    // when
    lockManager.ensureLockDefault();

    //then
    assertUpdateCallIfSameOwner(expirationAt, 0);
  }

  @Test(expected = LockCheckException.class)
  public void shouldNotWaitAndThrowException_IfLockHeldByOtherProcess_IfEnsureLock() throws LockPersistenceException, LockCheckException {
    //given
    long expiresAt = 3000L;
    long waitingTime = 1000L;

    Date newExpirationAt = new Date(100000L);
    when(timeService.currentDatePlusMillis(anyLong()))
        .thenReturn(newExpirationAt);
    when(timeService.currentTime())
        .thenReturn(new Date(40001L))
        .thenReturn(new Date(expiresAt - waitingTime));
    setLockManager(lockActiveMillis, quitTryingAfterMillis, tryFrequency);
    when(lockRepository.findByKey(anyString())).thenReturn(createFakeLockWithOtherOwner(expiresAt));
    doNothing().when(lockRepository).insertUpdate(any(LockEntry.class));
    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail")).doNothing().when(lockRepository).updateIfSameOwner(any(LockEntry.class));


    // when
    lockManager.ensureLockDefault();

  }

  @Test
  public void shouldTryAgain_IfNeedsRefresh_whenEnsureLock() throws LockPersistenceException, LockCheckException {
    //given
    long expiresAt = 3000L;
    long waitingTime = 1000L;
    Date newExpirationAt = new Date(40000L);
    when(timeService.currentDatePlusMillis(anyLong())).thenReturn(newExpirationAt);
    when(timeService.currentTime())
        .thenReturn(new Date(40000L))
        .thenReturn(new Date(expiresAt - waitingTime));
    setLockManager(3 * 1000L, quitTryingAfterMillis, tryFrequency);// 3 seconds. Margin should 1 second
    doNothing().when(lockRepository).insertUpdate(any(LockEntry.class));
    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail")).doNothing()
        .when(lockRepository).updateIfSameOwner(any(LockEntry.class));

    when(lockRepository.findByKey(anyString()))
        .thenReturn(new LockEntry(lockManager.getDefaultKey(), LockStatus.LOCK_HELD.name(), lockManager.getOwner(), new Date(expiresAt)));

    lockManager.acquireLockDefault();

    // when
    long timeBeforeCall = System.currentTimeMillis();
    lockManager.ensureLockDefault();
    long timeSpent = System.currentTimeMillis() - timeBeforeCall;

    //then
    assertTrue("Checker should wait that long", timeSpent < waitingTime);
    assertUpdateCallIfSameOwner(newExpirationAt, 1);
  }

  @Test
  public void shouldStopTrying_ifQuitTryingIsOver_WhenEnsureLock() throws LockPersistenceException {
    //given
    long expiresAt = 3000L;
    long waitingTime = 1;

    Date newExpirationAt = new Date(100000L);
    when(timeService.currentDatePlusMillis(anyLong())).thenReturn(newExpirationAt);
    when(timeService.currentTime()).thenReturn(new Date(expiresAt - waitingTime));

    when(timeService.nowPlusMillis(anyLong())).thenReturn(DONT_CARE_INSTANT);
    doReturn(false, false, true)
        .when(timeService).isPast(any(Instant.class));
    setLockManager(lockActiveMillis, quitTryingAfterMillis, tryFrequency);
    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail")).when(lockRepository).updateIfSameOwner(any(LockEntry.class));
    when(lockRepository.findByKey(anyString())).thenReturn(createFakeLockWithSameOwner(expiresAt));

    // when
    try {
      lockManager.ensureLockDefault();
    } catch (LockCheckException ex) {
      assertUpdateCallIfSameOwner(newExpirationAt, 3);
      return;
    }
    fail();

  }

  @Test
  public void shouldCallRepository_ifLockHeld_WhenReleaseLock() {
    //given
    when(timeService.currentDatePlusMillis(anyLong())).thenReturn(new Date(1000000L));
    when(timeService.currentTime()).thenReturn(new Date(0L));
    setLockManager(lockActiveMillis, quitTryingAfterMillis, tryFrequency);

    //when
    lockManager.acquireLockDefault();
    lockManager.releaseLockDefault();

    //then
    verify(lockRepository).removeByKeyAndOwner(lockManager.getDefaultKey(), lockManager.getOwner());
  }

  @Test
  public void shouldWriteTheLockInDB_IfLockIsReleased_WhenAcquireLock() throws LockPersistenceException, LockCheckException {
    //given
    when(timeService.currentDatePlusMillis(anyLong())).thenReturn(new Date(1000000L));
    when(timeService.currentTime()).thenReturn(new Date(0L));
    setLockManager(lockActiveMillis, quitTryingAfterMillis, tryFrequency);

    //when
    lockManager.acquireLockDefault();
    lockManager.releaseLockDefault();
    lockManager.acquireLockDefault();//TODO: Randomly produces a race condition by calling twice "lockRepository.removeByKeyAndOwner" (maybe LockDaemon)

    //then
    verify(lockRepository).removeByKeyAndOwner(lockManager.getDefaultKey(), lockManager.getOwner());
    verify(lockRepository, new Times(2)).insertUpdate(any(LockEntry.class));
  }

  @Test
  public void shouldWriteTheLockInDB_IfLockIsReleased_WhenEnsureLock() throws LockPersistenceException, LockCheckException {
    //given
    when(timeService.currentDatePlusMillis(anyLong())).thenReturn(new Date(1000000L));
    when(timeService.currentTime()).thenReturn(new Date(0L));
    setLockManager(lockActiveMillis, quitTryingAfterMillis, tryFrequency);

    //when
    lockManager.acquireLockDefault();
    lockManager.releaseLockDefault();
    lockManager.ensureLockDefault();

    //then
    verify(lockRepository).removeByKeyAndOwner(lockManager.getDefaultKey(), lockManager.getOwner());
    verify(lockRepository, new Times(1)).updateIfSameOwner(any(LockEntry.class));
  }

  @Test
  public void shouldReturnSameValue_IfSetValue_WhenGetFrequency() {
    //given
    setLockManager(lockActiveMillis, quitTryingAfterMillis, 3000L);

    //then
    assertEquals(3000L, lockManager.getLockTryFrequency());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalException_WhenFrequencyIsLessOrEqualMinimum() {

    setLockManager(lockActiveMillis, quitTryingAfterMillis, 499L);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalException_WhenQuitTryingIsLessOrEqualZero() {
    setLockManager(lockActiveMillis, 0, tryFrequency);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalException_WhenAcquiredForLessThanMinimum() {

    setLockManager(2999L, quitTryingAfterMillis, tryFrequency);
  }

  @Test
  public void shouldReturnFalse_IfNotStarted_WhenIsLockHeld() {
    //when
    setLockManager(lockActiveMillis, quitTryingAfterMillis, tryFrequency);
    boolean lockHeld = lockManager.isLockHeld();

    //then
    assertFalse(lockHeld);
  }

  @Test
  public void shouldReturnTrue_IfStarted_WhenIsLockHeld() throws LockCheckException {
    //given
    when(timeService.currentDatePlusMillis(anyLong())).thenReturn(new Date(1000000L));
    when(timeService.currentTime()).thenReturn(new Date(0L));
    setLockManager(lockActiveMillis, quitTryingAfterMillis, tryFrequency);
    lockManager.acquireLockDefault();

    //when
    boolean lockHeld = lockManager.isLockHeld();

    //then
    assertTrue(lockHeld);
  }

  private void assertInsertUpdateCall(Date expirationAt, int invocationTimes)
      throws LockPersistenceException {
    assertDao(expirationAt, invocationTimes, false);
  }

  private void assertUpdateCallIfSameOwner(Date expirationAt, int invocationTimes)
      throws LockPersistenceException {
    assertDao(expirationAt, invocationTimes, true);
  }

  private void assertDao(Date expirationAt, int invocationTimes, boolean onlyIfSameOwner) throws LockPersistenceException {
    ArgumentCaptor<LockEntry> captor = ArgumentCaptor.forClass(LockEntry.class);
    if (onlyIfSameOwner) {
      verify(lockRepository, new Times(invocationTimes)).updateIfSameOwner(captor.capture());
    } else {
      verify(lockRepository, new Times(invocationTimes)).insertUpdate(captor.capture());
    }
    if (invocationTimes > 0) {
      LockEntry saved = captor.getValue();
      assertEquals("Lock was saved with the wrong key", lockManager.getDefaultKey(), saved.getKey());
      assertEquals("Lock was saved with the wrong status", LockStatus.LOCK_HELD.name(), saved.getStatus());
      assertEquals("lock was saved with the wrong owner", lockManager.getOwner(), saved.getOwner());
      assertEquals("Lock was saved with the wrong expires time", expirationAt, saved.getExpiresAt());
    }
  }

  private LockEntry createFakeLockWithOtherOwner(long expiresAt) {
    return createFakeLock(expiresAt, "otherOwner");
  }

  private LockEntry createFakeLockWithSameOwner(long expiresAt) {
    return createFakeLock(expiresAt, lockManager.getOwner());
  }

  private LockEntry createFakeLock(long expiresAt, String owner) {
    return new LockEntry(lockManager.getDefaultKey(), LockStatus.LOCK_HELD.name(), owner, new Date(expiresAt));
  }
}
