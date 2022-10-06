package io.mongock.driver.remote.driver;

import io.mongock.driver.api.driver.ChangeSetDependency;
import io.mongock.driver.api.entry.ChangeEntryService;
import io.mongock.driver.core.driver.NonTransactionalConnectionDriverBase;
import io.mongock.driver.core.lock.LockRepository;
import io.mongock.driver.remote.repository.RemoteChangeEntryRepository;
import io.mongock.driver.remote.repository.RemoteLockRepository;
import io.mongock.driver.remote.repository.external.ChangeEntryServiceClient;
import io.mongock.driver.remote.repository.external.LockServiceClient;

import static io.mongock.utils.Constants.DEFAULT_LOCK_ACQUIRED_FOR_MILLIS;
import static io.mongock.utils.Constants.DEFAULT_QUIT_TRYING_AFTER_MILLIS;
import static io.mongock.utils.Constants.DEFAULT_TRY_FREQUENCY_MILLIS;

public class RemoteDriver extends NonTransactionalConnectionDriverBase {

  private final RemoteChangeEntryRepository changeEntryRepository;
  private final RemoteLockRepository lockRepository;

  public static RemoteDriver withDefaultLock(String organization, String service, String remoteHost) {
    return RemoteDriver.withLockStrategy(
        organization,
        service,
        remoteHost,
        DEFAULT_LOCK_ACQUIRED_FOR_MILLIS,
        DEFAULT_QUIT_TRYING_AFTER_MILLIS,
        DEFAULT_TRY_FREQUENCY_MILLIS);
  }

  public static RemoteDriver withLockStrategy(String organization,
                                              String service,
                                              String remoteHost,
                                              long lockAcquiredForMillis,
                                              long lockQuitTryingAfterMillis,
                                              long lockTryFrequencyMillis) {
    return new RemoteDriver(
        new RemoteChangeEntryRepository(ChangeEntryServiceClient.getClient(remoteHost), organization, service),
        new RemoteLockRepository(LockServiceClient.getClient(remoteHost), organization, service),
        lockAcquiredForMillis,
        lockQuitTryingAfterMillis,
        lockTryFrequencyMillis);
  }

  protected RemoteDriver(RemoteChangeEntryRepository changeEntryRepository,
                         RemoteLockRepository lockRepository,
                         long lockAcquiredForMillis,
                         long lockQuitTryingAfterMillis,
                         long lockTryFrequencyMillis) {
    super(lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
    this.changeEntryRepository = changeEntryRepository;
    this.lockRepository = lockRepository;
  }

  @Override
  public ChangeEntryService getChangeEntryService() {
    return changeEntryRepository;
  }

  @Override
  protected LockRepository getLockRepository() {
    return lockRepository;
  }

  protected void specificInitialization() {
    //TODO not mandatory
    dependencies.add(new ChangeSetDependency(ChangeEntryService.class, getChangeEntryService(), false));
  }
}
