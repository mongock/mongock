package io.mongock.driver.core.lock;

import io.mongock.driver.api.lock.LockCheckException;
import io.mongock.driver.api.lock.LockManager;
import io.mongock.utils.TimeService;
import io.mongock.utils.annotation.NotThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * <p>This class is responsible of managing the lock at high level. It provides 3 main methods which are
 * for acquiring, ensuring(doesn't acquires the lock, just refresh the expiration time if required) and
 * releasing the lock.</p>
 */
@NotThreadSafe
public class DefaultLockManager implements LockManager {

  //static constants
  private static final Logger logger = LoggerFactory.getLogger(DefaultLockManager.class);

  private static final String GOING_TO_SLEEP_MSG = "Mongock is going to sleep to wait for the lock:  {} ms({} minutes)";
  private static final String EXPIRATION_ARG_ERROR_MSG = "Lock expiration period must be greater than %d ms";
  private static final String MAX_TRIES_ERROR_TEMPLATE = "Quit trying lock after %s millis due to LockPersistenceException: \n\tcurrent lock:  %s\n\tnew lock: %s\n\tacquireLockQuery: %s\n\tdb error detail: %s";
  private static final String LOCK_HELD_BY_OTHER_PROCESS = "Lock held by other process. Cannot ensure lock.\n\tcurrent lock:  %s\n\tnew lock: %s\n\tacquireLockQuery: %s\n\tdb error detail: %s";

  private static final long MIN_LOCK_ACQUIRED_FOR_MILLIS = 3 * 1000L;// 3 seconds
  private static final long DEFAULT_LOCK_ACQUIRED_FOR_MILLIS = 60 * 1000L;// 1 minute

  private static final long DEFAULT_QUIT_TRY_AFTER_MILLIS = 3 * DEFAULT_LOCK_ACQUIRED_FOR_MILLIS;//3 times default min acquired

  private static final double LOCK_REFRESH_MARGIN_PERCENTAGE = 0.33;// 30%
  private static final long MIN_LOCK_REFRESH_MARGIN_MILLIS = 1000L;// 1 second
  private static final long DEFAULT_LOCK_REFRESH_MARGIN_MILLIS = (long) (DEFAULT_LOCK_ACQUIRED_FOR_MILLIS * LOCK_REFRESH_MARGIN_PERCENTAGE);

  private static final long MINIMUM_WAITING_TO_TRY_AGAIN = 500L;//Half a second
  private static final long DEFAULT_TRY_FREQUENCY_MILLIS = 1000L;// ! second


  //injections
  private final LockRepository repository;
  private final TimeService timeUtils;
  /**
   * Owner of the lock
   */
  private final String owner;

  /**
   * Daemon that will ensure the lock in background.
   */
  private LockDaemon lockDaemon;

  /**
   * <p>The period of time for which the lock will be owned.</p>
   */
  private final long lockAcquiredForMillis;

  /**
   * <p>Milliseconds after which it will try to acquire the lock again<p/>
   */
  private final long lockTryFrequencyMillis;

  /**
   * <p>The margin in which the lock should be refresh to avoid losing it</p>
   */
  private final long lockRefreshMarginMillis;
  /**
   * <p>Maximum time it will wait for the lock in total.</p>
   */
  private final long lockQuitTryingAfterMillis;


  /**
   * Moment when will mandatory to acquire the lock again.
   */
  private volatile Date lockExpiresAt = null;

  /**
   * The instant the acquisition has shouldStopTryingAt
   */
  private volatile Instant shouldStopTryingAt;

  public static DefaultLockManagerBuilder builder() {
    return new DefaultLockManagerBuilder();
  }

  /**
   * Constructor takes some bean injections
   *
   * @param repository lock repository
   * @param timeUtils  time utils service
   */
  //TODO add lock configuration to constructor, make fields finals and move DEFAULTS away
  public DefaultLockManager(LockRepository repository,
                            TimeService timeUtils,
                            long lockAcquiredForMillis,
                            long lockQuitTryingAfterMillis,
                            long lockTryFrequencyMillis,
                            long lockRefreshMarginMillis) {
    this.repository = repository;
    this.timeUtils = timeUtils;
    this.lockAcquiredForMillis = lockAcquiredForMillis;
    this.lockQuitTryingAfterMillis = lockQuitTryingAfterMillis;
    this.lockTryFrequencyMillis = lockTryFrequencyMillis;
    this.lockRefreshMarginMillis = lockRefreshMarginMillis;
    this.owner = UUID.randomUUID().toString();//TODO reconsider this
  }

  /**
   * <p>Tries to acquire the default lock regardless who is the current owner.</p>
   * <p>If the lock is already acquired by the current LockChecker or is expired, will be updated</p>
   * <p>In case the lock is acquired by another LockChecker, it will wait until the current lock is expired
   * and will try to acquire it again. This will be repeated as many times as (maxTries - 1)</p>
   *
   * @throws LockCheckException if the lock cannot be acquired
   */
  public void acquireLockDefault() throws LockCheckException {
    acquireLock(getDefaultKey());
  }

  private void acquireLock(String lockKey) throws LockCheckException {
    boolean keepLooping = true;
    do {
      try {
        logger.info("Mongock trying to acquire the lock");
        Date newLockExpiresAt = timeUtils.currentDatePlusMillis(lockAcquiredForMillis);
        repository.upsert(new LockEntry(lockKey, LockStatus.LOCK_HELD.name(), owner, newLockExpiresAt));
        logger.info("Mongock acquired the lock until: {}", newLockExpiresAt);
        updateStatus(newLockExpiresAt);
        lockDaemon.activate();
        keepLooping = false;
      } catch (LockPersistenceException ex) {
        handleLockException(true, ex);
      }
    } while (keepLooping);
  }

  /**
   * <p>Tries to refresh the default lock when the current LockChecker has the lock or , when the lock
   * is expired, is the last owner</p>
   * <p>Notice that it does not try to acquire when is acquired by another LockChecker</p>
   *
   * @throws LockCheckException if, in case of needed, the lock cannot be refreshed
   */
  public void ensureLockDefault() throws LockCheckException {
    ensureLock(getDefaultKey());
  }

  private void ensureLock(String lockKey) throws LockCheckException {
    boolean keepLooping = true;
    do {
      if (needsRefreshLock()) {
        try {
          logger.info("Mongock trying to refresh the lock");
          Date lockExpiresAtTemp = timeUtils.currentDatePlusMillis(lockAcquiredForMillis);
          LockEntry lockEntry = new LockEntry(lockKey, LockStatus.LOCK_HELD.name(), owner, lockExpiresAtTemp);
          repository.updateIfSameOwner(lockEntry);
          updateStatus(lockExpiresAtTemp);
          logger.info("Mongock refreshed the lock until: {}", lockExpiresAtTemp);
          lockDaemon.activate();
          keepLooping = false;
        } catch (LockPersistenceException ex) {
          handleLockException(false, ex);
        }
      } else {
        keepLooping = false;
      }
    } while (keepLooping);
  }

  /**
   * <p>Release the default lock when is acquired by the current LockChecker.</p>
   * <p>When the lock is not acquired by the current LockChecker, it won't make any change.
   * Does not throw any exception neither.</p>
   * <p>Idempotent operation.</p>
   */
  public void releaseLockDefault() {
    releaseLock(getDefaultKey());
  }

  /**
   * Required when lock managing is done.
   * Responsible for:
   * - Release the lock in database (not critical. It will be expired eventually )
   * - Cancel the lock daemon (CRITICAL. Otherwise it  will keep refreshing the lock making other Mongock process starved)
   */
  @Override
  public void close() {
    releaseLockDefault();
  }

  private synchronized void releaseLock(String lockKey) {
    if (lockDaemon != null) {
      lockDaemon.cancel();
    }
    if (this.lockExpiresAt == null) {
      return;
    }
    logger.info("Mongock releasing the lock");
    repository.removeByKeyAndOwner(lockKey, this.getOwner());
    lockExpiresAt = null;
    shouldStopTryingAt = Instant.now();//this makes the ensureLock to fail
    logger.info("Mongock released the lock");
  }


  @Override
  public long getLockTryFrequency() {
    return lockTryFrequencyMillis;
  }

  private void handleLockException(boolean acquiringLock, LockPersistenceException ex) {
    LockEntry currentLock = repository.findByKey(getDefaultKey());

    if (isAcquisitionTimerOver()) {
      updateStatus(null);
      throw new LockCheckException(String.format(
          MAX_TRIES_ERROR_TEMPLATE,
          lockQuitTryingAfterMillis,
          currentLock != null ? currentLock.toString() : "none",
          ex.getNewLockEntity(),
          ex.getAcquireLockQuery(),
          ex.getDbErrorDetail()));
    }

    if (isLockOwnedByOtherProcess(currentLock)) {
      Date currentLockExpiresAt = currentLock.getExpiresAt();
      logger.warn("Lock is taken by other process until: {}", currentLockExpiresAt);
      if (!acquiringLock) {
        throw new LockCheckException(String.format(
            LOCK_HELD_BY_OTHER_PROCESS,
            currentLock.toString(),
            ex.getNewLockEntity(),
            ex.getAcquireLockQuery(),
            ex.getDbErrorDetail()));
      }

      waitForLock(currentLockExpiresAt);
    }

  }

  private boolean isLockOwnedByOtherProcess(LockEntry currentLock) {
    return currentLock != null && !currentLock.isOwner(owner);
  }

  private void waitForLock(Date expiresAtMillis) {
    Date current = timeUtils.currentTime();
    long currentLockWillExpireInMillis = expiresAtMillis.getTime() - current.getTime();
    long sleepingMillis = lockTryFrequencyMillis;
    if (lockTryFrequencyMillis > currentLockWillExpireInMillis) {
      logger.info("The configured time frequency[{} millis] is higher than the current lock's expiration", lockTryFrequencyMillis);
      sleepingMillis = currentLockWillExpireInMillis > MINIMUM_WAITING_TO_TRY_AGAIN ? currentLockWillExpireInMillis : MINIMUM_WAITING_TO_TRY_AGAIN;
    }
    logger.info("Mongock will try to acquire the lock in {} mills", sleepingMillis);


    try {
      logger.info(GOING_TO_SLEEP_MSG, sleepingMillis, timeUtils.millisToMinutes(sleepingMillis));
      Thread.sleep(sleepingMillis);
    } catch (InterruptedException ex) {
      logger.error("ERROR acquiring the lock", ex);
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public String getOwner() {
    return owner;
  }

  public boolean isLockHeld() {
    return lockExpiresAt != null && timeUtils.currentTime().compareTo(lockExpiresAt) < 1;
  }

  @Override
  public long getMillisUntilRefreshRequired() {
    if (lockExpiresAt != null) {
      return lockExpiresAt.getTime() - timeUtils.currentTime().getTime() - lockRefreshMarginMillis;
    } else {
      return lockAcquiredForMillis - lockRefreshMarginMillis;
    }
  }

  private boolean needsRefreshLock() {

    if (this.lockExpiresAt == null) {
      return true;
    }
    Date currentTime = timeUtils.currentTime();
    Date expirationWithMargin = new Date(this.lockExpiresAt.getTime() - lockRefreshMarginMillis);
    return currentTime.compareTo(expirationWithMargin) >= 0;
  }

  private void updateStatus(Date lockExpiresAt) {
    this.lockExpiresAt = lockExpiresAt;
    finishAcquisitionTimer();
  }

  /**
   * idempotent operation that
   * - Starts the acquisition timer
   * - Initializes and run the lock daemon.
   */
  protected void initialize() {
    if (shouldStopTryingAt == null) {
      shouldStopTryingAt = timeUtils.nowPlusMillis(lockQuitTryingAfterMillis);
    }
    if (lockDaemon == null) {
      lockDaemon = new LockDaemon(this);
      lockDaemon.start();
    }
  }

  /**
   * idempotent operation to start the acquisition timer
   */
  private void finishAcquisitionTimer() {
    shouldStopTryingAt = null;
  }


  private boolean isAcquisitionTimerOver() {
    return timeUtils.isPast(shouldStopTryingAt);
  }


  public static class DefaultLockManagerBuilder {

    private LockRepository lockRepository;
    private long lockAcquiredForMillis = DEFAULT_LOCK_ACQUIRED_FOR_MILLIS;
    private long lockTryFrequencyMillis = DEFAULT_TRY_FREQUENCY_MILLIS;
    private long lockRefreshMarginMillis = DEFAULT_LOCK_REFRESH_MARGIN_MILLIS;
    private long lockQuitTryingAfterMillis = DEFAULT_QUIT_TRY_AFTER_MILLIS;
    private TimeService timeService = new TimeService();

    public DefaultLockManagerBuilder() {
    }

    /**
     * <p>If the flag 'waitForLog' is set, indicates the maximum time it will wait for the lock in total.</p>
     *
     * @param millis max waiting time for lock. Must be greater than 0
     * @return LockChecker object for fluent interface
     */
    public DefaultLockManagerBuilder setLockQuitTryingAfterMillis(long millis) {
      if (millis <= 0) {
        throw new IllegalArgumentException("Lock-quit-trying-after must be grater than 0 ");
      }
      lockQuitTryingAfterMillis = millis;
      return this;
    }

    /**
     * <p>Updates the maximum number of tries to acquire the lock, if the flag 'waitForLog' is set </p>
     * <p>Default 1</p>
     *
     * @param millis number of tries
     * @return LockChecker object for fluent interface
     */
    public DefaultLockManagerBuilder setLockTryFrequencyMillis(long millis) {
      if (millis < MINIMUM_WAITING_TO_TRY_AGAIN) {
        throw new IllegalArgumentException(String.format("Lock-try-frequency must be grater than %d", MINIMUM_WAITING_TO_TRY_AGAIN));
      }
      lockTryFrequencyMillis = millis;
      return this;
    }

    /**
     * <p>Indicates the number of milliseconds the lock will be acquired for</p>
     * <p>Minimum 3 seconds</p>
     *
     * @param millis milliseconds the lock will be acquired for
     * @return LockChecker object for fluent interface
     */
    public DefaultLockManagerBuilder setLockAcquiredForMillis(long millis) {
      if (millis < MIN_LOCK_ACQUIRED_FOR_MILLIS) {
        throw new IllegalArgumentException(String.format(EXPIRATION_ARG_ERROR_MSG, MIN_LOCK_ACQUIRED_FOR_MILLIS));
      }
      lockAcquiredForMillis = millis;
      long marginTemp = (long) (lockAcquiredForMillis * LOCK_REFRESH_MARGIN_PERCENTAGE);
      lockRefreshMarginMillis = Math.max(marginTemp, MIN_LOCK_REFRESH_MARGIN_MILLIS);
      return this;
    }

    public DefaultLockManagerBuilder setLockRepository(LockRepository lockRepository) {
      this.lockRepository = lockRepository;
      return this;
    }

    public DefaultLockManagerBuilder setTimeUtils(TimeService timeService) {
      this.timeService = timeService;
      return this;
    }

    public DefaultLockManager build() {
      DefaultLockManager lockManager = new DefaultLockManager(
          lockRepository,
          timeService,
          lockAcquiredForMillis,
          lockQuitTryingAfterMillis,
          lockTryFrequencyMillis,
          lockRefreshMarginMillis);
      lockManager.initialize();
      return lockManager;
    }


  }
}
