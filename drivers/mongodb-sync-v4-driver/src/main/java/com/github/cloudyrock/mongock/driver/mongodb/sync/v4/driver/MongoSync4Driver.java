package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.driver;

import com.mongodb.client.MongoDatabase;
import io.changock.driver.api.driver.ForbiddenParametersMap;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.entry.ChangeEntryService;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.repository.MongoSync4ChangeEntryRepository;
import io.changock.utils.annotation.NotThreadSafe;

@NotThreadSafe
public class MongoSync4Driver extends MongoSync4DriverBase<ChangeEntry> {

  private static final ForbiddenParametersMap FORBIDDEN_PARAMETERS_MAP = new ForbiddenParametersMap();

  protected MongoSync4ChangeEntryRepository<ChangeEntry> changeEntryRepository;

  public static MongoSync4Driver withDefaultLock(MongoDatabase mongoDatabase) {
    return new MongoSync4Driver(mongoDatabase, 3L, 4L, 3);
  }

  public static MongoSync4Driver withLockSetting(MongoDatabase mongoDatabase,
                                                 long lockAcquiredForMinutes,
                                                 long maxWaitingForLockMinutes,
                                                 int maxTries) {
    return new MongoSync4Driver(mongoDatabase, lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries);
  }

  protected MongoSync4Driver(MongoDatabase mongoDatabase,
                             long lockAcquiredForMinutes,
                             long maxWaitingForLockMinutes,
                             int maxTries) {
    super(mongoDatabase, lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries);
  }

  @Override
  public ChangeEntryService<ChangeEntry> getChangeEntryService() {
    if (changeEntryRepository == null) {
      this.changeEntryRepository = new MongoSync4ChangeEntryRepository<>(mongoDatabase.getCollection(changeLogCollectionName), indexCreation);
    }
    return changeEntryRepository;
  }

  @Override
  public ForbiddenParametersMap getForbiddenParameters() {
    return FORBIDDEN_PARAMETERS_MAP;
  }
}
