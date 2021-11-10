package io.mongock.driver.core.driver;

import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.api.entry.ChangeEntryService;
import io.mongock.driver.api.lock.LockManager;
import io.mongock.driver.core.lock.DefaultLockManager;
import io.mongock.driver.core.lock.LockRepository;
import io.mongock.utils.TimeService;
import io.mongock.utils.annotation.NotThreadSafe;

@NotThreadSafe
public abstract class ConnectionDriverBase implements ConnectionDriver {
  private static final String DEFAULT_MIGRATION_REPOSITORY_NAME = "mongockChangeLog";
  private static final String DEFAULT_LOCK_REPOSITORY_NAME = "mongockLock";

  private static final TimeService TIME_SERVICE = new TimeService();

  //Lock
  protected final long lockAcquiredForMillis;
  protected final long lockQuitTryingAfterMillis;
  protected final long lockTryFrequencyMillis;

  protected boolean initialized = false;
  protected LockManager lockManager = null;
  protected String migrationRepositoryName2;
  protected String lockRepositoryName2 ;
  protected boolean indexCreation = true;


  protected ConnectionDriverBase(long lockAcquiredForMillis, long lockQuitTryingAfterMillis, long lockTryFrequencyMillis) {
    this.lockAcquiredForMillis = lockAcquiredForMillis;
    this.lockQuitTryingAfterMillis = lockQuitTryingAfterMillis;
    this.lockTryFrequencyMillis = lockTryFrequencyMillis;
  }

  @Override
  public final void initialize() {
    if (!initialized) {
      initialized = true;
      LockRepository lockRepository = this.getLockRepository();
      lockRepository.initialize();
      lockManager = DefaultLockManager.builder()
          .setLockRepository(lockRepository)
          .setLockAcquiredForMillis(lockAcquiredForMillis)
          .setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis)
          .setLockTryFrequencyMillis(lockTryFrequencyMillis)
          .build();
      ChangeEntryService changeEntryService = getChangeEntryService();
      changeEntryService.initialize();
      specificInitialization();
    }
  }

  @Override
  public LockManager getLockManager() {
    if (lockManager == null) {
      throw new MongockException("Internal error: Driver needs to be initialized by the runner");
    }
    return lockManager;
  }

  public boolean isInitialized() {
    return initialized;
  }

  @Override
  public void setMigrationRepositoryName(String migrationRepositoryName) {
    if(migrationRepositoryName != null || this.migrationRepositoryName2 == null) {
      this.migrationRepositoryName2 = migrationRepositoryName;
    }
  }

  @Override
  public void setLockRepositoryName(String lockRepositoryName) {
    if(lockRepositoryName != null || this.lockRepositoryName2 == null) {
      this.lockRepositoryName2 = lockRepositoryName;
    }
  }

  @Override
  public String getMigrationRepositoryName() {
    return migrationRepositoryName2 != null ? migrationRepositoryName2 : DEFAULT_MIGRATION_REPOSITORY_NAME;
  };

  @Override
  public String getLockRepositoryName() {

    return lockRepositoryName2 != null ? lockRepositoryName2 : DEFAULT_LOCK_REPOSITORY_NAME;
  }


  public boolean isIndexCreation() {
    return indexCreation;
  }

  public void setIndexCreation(boolean indexCreation) {
    this.indexCreation = indexCreation;
  }

  protected abstract LockRepository getLockRepository();

  protected void specificInitialization() {
    //TODO not mandatory
  }

  @Override
  public void runValidation() throws MongockException {
  }


  @Deprecated
  public void setChangeLogRepositoryName(String migrationRepositoryName) {
    setMigrationRepositoryName(migrationRepositoryName);
  }

}
