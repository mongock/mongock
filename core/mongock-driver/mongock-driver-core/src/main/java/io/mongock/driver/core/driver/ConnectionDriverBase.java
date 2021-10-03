package io.mongock.driver.core.driver;

import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.driver.api.entry.ChangeEntryService;
import io.mongock.driver.api.lock.LockManager;
import io.mongock.driver.core.lock.DefaultLockManager;
import io.mongock.driver.core.lock.LockRepository;
import io.mongock.utils.TimeService;
import io.mongock.utils.annotation.NotThreadSafe;

@NotThreadSafe
public abstract class ConnectionDriverBase<CHANGE_ENTRY extends ChangeEntry> implements ConnectionDriver<CHANGE_ENTRY> {

  private static final TimeService TIME_SERVICE = new TimeService();

  //Lock
  protected final long lockAcquiredForMillis;
  protected final long lockQuitTryingAfterMillis;
  protected final long lockTryFrequencyMillis;

  protected boolean initialized = false;
  protected LockManager lockManager = null;
  protected String migrationRepositoryName;
  protected String lockRepositoryName;
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
      lockRepository.setIndexCreation(isIndexCreation());
      lockRepository.initialize();
      lockManager = new DefaultLockManager(lockRepository, TIME_SERVICE)
          .setLockAcquiredForMillis(lockAcquiredForMillis)
          .setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis)
          .setLockTryFrequencyMillis(lockTryFrequencyMillis);
      ChangeEntryService<CHANGE_ENTRY> changeEntryService = getChangeEntryService();
      changeEntryService.setIndexCreation(isIndexCreation());
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

  public void setMigrationRepositoryName(String migrationRepositoryName) {
    this.migrationRepositoryName = migrationRepositoryName;
  }

  public void setLockRepositoryName(String lockRepositoryName) {
    this.lockRepositoryName = lockRepositoryName;
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
