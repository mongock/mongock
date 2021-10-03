package io.mongock.driver.core.driver;


import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.driver.ChangeSetDependency;
import io.mongock.driver.api.driver.Transactioner;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.driver.api.entry.ChangeEntryService;
import io.mongock.driver.api.lock.LockManager;
import io.mongock.driver.core.lock.LockRepositoryWithEntity;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import java.util.Optional;
import java.util.Set;

public class ConnectionDriverBaseTest {


  @Test
  public void shouldInitializeRepositories() {
    // given
    LockRepositoryWithEntity lockRepository = Mockito.mock(LockRepositoryWithEntity.class);
    ChangeEntryService<ChangeEntry> changeEntryService = Mockito.mock(ChangeEntryService.class);

    ConnectionDriverBase<ChangeEntry> driver = new ConnectionDriverBaseTestImpl(
        4,
        3,
        3,
        lockRepository,
        changeEntryService,
        Mockito.mock(LockManager.class));

    // when
    driver.initialize();

    // then
    Mockito.verify(lockRepository, new Times(1)).initialize();
    Mockito.verify(changeEntryService, new Times(1)).initialize();


  }


  static class ConnectionDriverBaseTestImpl extends ConnectionDriverBase<ChangeEntry> {

    private final LockRepositoryWithEntity lockRepository;
    private final ChangeEntryService<ChangeEntry> changeEntryService;
    private final LockManager lockManager;


    ConnectionDriverBaseTestImpl(long lockAcquiredForMinutes,
                                 long maxWaitingForLockMinutesEachTry,
                                 int maxTries,
                                 LockRepositoryWithEntity lockRepository,
                                 ChangeEntryService<ChangeEntry> changeEntryService,
                                 LockManager lockManager) {
      super(
          minutesToMillis(lockAcquiredForMinutes),
          minutesToMillis(maxWaitingForLockMinutesEachTry * maxTries),
          1000L
      );
      this.lockRepository = lockRepository;
      this.changeEntryService = changeEntryService;
      this.lockManager = lockManager;
    }

    private static long minutesToMillis(long minutes) {
      return minutes * 60 * 1000;
    }

    @Override
    protected LockRepositoryWithEntity getLockRepository() {
      return lockRepository;
    }

    @Override
    protected void specificInitialization() {

    }

    @Override
    public ChangeEntryService<ChangeEntry> getChangeEntryService() {
      return changeEntryService;
    }

    @Override
    public LockManager getLockManager() {
      return lockManager;
    }

    @Override
    public Set<ChangeSetDependency> getDependencies() {
      return null;
    }

    @Override
    public Class<?> getLegacyMigrationChangeLogClass(boolean runAlways) {
      return null;
    }

    @Override
    public void runValidation() throws MongockException {

    }



    @Override
    public Optional<Transactioner> getTransactioner() {
      return Optional.empty();
    }
  }


}
