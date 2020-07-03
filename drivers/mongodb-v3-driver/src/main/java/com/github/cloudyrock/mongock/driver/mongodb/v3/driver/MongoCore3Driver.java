package com.github.cloudyrock.mongock.driver.mongodb.v3.driver;

import com.mongodb.client.MongoDatabase;
import io.changock.driver.api.driver.ForbiddenParametersMap;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.entry.ChangeEntryService;
import com.github.cloudyrock.mongock.driver.mongodb.v3.repository.Mongo3ChangeEntryRepository;
import io.changock.utils.annotation.NotThreadSafe;

@NotThreadSafe
public class MongoCore3Driver extends MongoCore3DriverBase<ChangeEntry> {

  private static final ForbiddenParametersMap FORBIDDEN_PARAMETERS_MAP = new ForbiddenParametersMap();

  protected Mongo3ChangeEntryRepository<ChangeEntry> changeEntryRepository;

  public static MongoCore3Driver withDefaultLock(MongoDatabase mongoDatabase) {
    return new MongoCore3Driver(mongoDatabase, 3L, 4L, 3);
  }

  public static MongoCore3Driver withLockSetting(MongoDatabase mongoDatabase,
                                                 long lockAcquiredForMinutes,
                                                 long maxWaitingForLockMinutes,
                                                 int maxTries) {
    return new MongoCore3Driver(mongoDatabase, lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries);
  }

  protected MongoCore3Driver(MongoDatabase mongoDatabase,
                             long lockAcquiredForMinutes,
                             long maxWaitingForLockMinutes,
                             int maxTries) {
    super(mongoDatabase, lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries);
  }

  @Override
  public ChangeEntryService<ChangeEntry> getChangeEntryService() {
    if (changeEntryRepository == null) {
      this.changeEntryRepository = new Mongo3ChangeEntryRepository<>(mongoDatabase.getCollection(changeLogCollectionName), indexCreation);
    }
    return changeEntryRepository;
  }

  @Override
  public ForbiddenParametersMap getForbiddenParameters() {
    return FORBIDDEN_PARAMETERS_MAP;
  }

}
