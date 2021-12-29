package io.mongock.driver.core.driver;


import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.driver.ChangeSetDependency;
import io.mongock.driver.api.driver.DriverLegaciable;
import io.mongock.driver.api.driver.Transactional;
import io.mongock.driver.api.entry.ChangeEntryService;
import io.mongock.driver.api.lock.LockManager;
import io.mongock.driver.core.lock.LockRepository;
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
    ChangeEntryService changeEntryService = Mockito.mock(ChangeEntryService.class);

    ConnectionDriverBase driver = new ConnectionDriverBaseTestImpl(
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


  static class ConnectionDriverBaseTestImpl extends ConnectionDriverBase implements DriverLegaciable {

    private final LockManager lockManager;


    ConnectionDriverBaseTestImpl(long lockAcquiredForMinutes,
                                 long maxWaitingForLockMinutesEachTry,
                                 int maxTries,
                                 LockRepository lockRepository,
                                 ChangeEntryService changeEntryRepository,
                                 LockManager lockManager) {
      super(
          minutesToMillis(lockAcquiredForMinutes),
          minutesToMillis(maxWaitingForLockMinutesEachTry * maxTries),
          1000L
      );
      this.lockRepository = lockRepository;
      this.changeEntryRepository = changeEntryRepository;
      this.lockManager = lockManager;
    }

    private static long minutesToMillis(long minutes) {
      return minutes * 60 * 1000;
    }


    @Override
    protected void afterParentInitialization() {

    }


    @Override
    public LockManager getLockManager() {
      return lockManager;
    }

    @Override
    protected void beforeParentInitialization() {
      changeEntryRepository.setIndexCreation(isIndexCreation());
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
    public Optional<Transactional> getTransactioner() {
      return Optional.empty();
    }
  }


}
