package io.mongock.driver.core.driver;

import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.driver.ChangeSetDependency;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.api.entry.ChangeEntryService;
import io.mongock.driver.api.lock.LockManager;
import io.mongock.driver.core.lock.DefaultLockManager;
import io.mongock.driver.core.lock.LockRepository;
import io.mongock.utils.annotation.NotThreadSafe;

import java.util.HashSet;
import java.util.Set;

@NotThreadSafe
public abstract class ConnectionDriverBase implements ConnectionDriver {
  private static final String DEFAULT_MIGRATION_REPOSITORY_NAME = "mongockChangeLog";
  private static final String DEFAULT_LOCK_REPOSITORY_NAME = "mongockLock";

  //Lock
  protected final long lockAcquiredForMillis;
  protected final long lockQuitTryingAfterMillis;
  protected final long lockTryFrequencyMillis;

  protected boolean initialized = false;
  protected LockRepository lockRepository;
  protected ChangeEntryService changeEntryRepository;
  protected LockManager lockManager = null;
  protected String migrationRepositoryName;
  protected String lockRepositoryName;
  protected boolean indexCreation = true;
  protected Set<ChangeSetDependency> dependencies;


  protected ConnectionDriverBase(long lockAcquiredForMillis, long lockQuitTryingAfterMillis, long lockTryFrequencyMillis) {
    this.lockAcquiredForMillis = lockAcquiredForMillis;
    this.lockQuitTryingAfterMillis = lockQuitTryingAfterMillis;
    this.lockTryFrequencyMillis = lockTryFrequencyMillis;
  }

  @Override
  public final synchronized void initialize() {
    if (!initialized) {
      beforeParentInitialization();
      commonInitialization();
      afterParentInitialization();
    }
  }

  protected abstract void beforeParentInitialization();

  protected void afterParentInitialization() {/*OPTIONAL**/}

  @Override
  public LockManager getLockManager() {
    if (lockManager == null) {
      throw new MongockException("Internal error: Driver needs to be initialized by the runner");
    }
    return lockManager;
  }

  @Override
  public ChangeEntryService getChangeEntryService() {
    if (changeEntryRepository == null) {
      throw new MongockException("Driver not initialized");
    }
    return changeEntryRepository;
  }

  @Override
  public final void setMigrationRepositoryName(String migrationRepositoryName) {
    if (migrationRepositoryName != null || this.migrationRepositoryName == null) {
      this.migrationRepositoryName = migrationRepositoryName;
    }
  }

  @Override
  public final void setLockRepositoryName(String lockRepositoryName) {
    if (lockRepositoryName != null || this.lockRepositoryName == null) {
      this.lockRepositoryName = lockRepositoryName;
    }
  }

  public boolean isIndexCreation() {
    return indexCreation;
  }

  public void setIndexCreation(boolean indexCreation) {
    this.indexCreation = indexCreation;
  }


  @Override
  public Set<ChangeSetDependency> getDependencies() {
    if (dependencies == null) {
      throw new MongockException("Driver not initialized");
    }
    return dependencies;
  }

  /*This should be injected as association**/
  protected void removeDependencyIfAssignableFrom(Set<ChangeSetDependency> dependencies, Class<?> type) {
    if (dependencies != null) {
      dependencies.removeIf(d -> type.isAssignableFrom(d.getType()));
    }
  }

  public final String getMigrationRepositoryName() {
    return migrationRepositoryName != null ? migrationRepositoryName : DEFAULT_MIGRATION_REPOSITORY_NAME;
  }

  public final String getLockRepositoryName() {
    return lockRepositoryName != null ? lockRepositoryName : DEFAULT_LOCK_REPOSITORY_NAME;
  }


  private void commonInitialization() {
    initialized = true;
    lockRepository.initialize();
    lockManager = DefaultLockManager.builder()
        .setLockRepository(lockRepository)
        .setLockAcquiredForMillis(lockAcquiredForMillis)
        .setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis)
        .setLockTryFrequencyMillis(lockTryFrequencyMillis)
        .build();
    changeEntryRepository.initialize();
    dependencies = new HashSet<>();
  }


  /*DEPRECATIONS**/
  @Deprecated
  public void setChangeLogRepositoryName(String migrationRepositoryName) {
    setMigrationRepositoryName(migrationRepositoryName);
  }
}
