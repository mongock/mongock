package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.driver;

import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.changelogs.runalways.MongockSync4LegacyMigrationChangeRunAlwaysLog;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.changelogs.runonce.MongockSync4LegacyMigrationChangeLog;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.repository.MongoSync4ChangeEntryRepository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.changock.driver.api.driver.ForbiddenParametersMap;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.entry.ChangeEntryService;
import io.changock.utils.annotation.NotThreadSafe;

@NotThreadSafe
public class MongoSync4Driver extends MongoSync4DriverBase<ChangeEntry> {

  private static final ForbiddenParametersMap FORBIDDEN_PARAMETERS_MAP = new ForbiddenParametersMap();

  protected MongoSync4ChangeEntryRepository<ChangeEntry> changeEntryRepository;

  public static MongoSync4Driver withDefaultLock(MongoClient mongoClient, String databaseName) {
    return new MongoSync4Driver(mongoClient, databaseName, 3L, 4L, 3);
  }

  public static MongoSync4Driver withLockSetting(MongoClient mongoClient,
                                                 String databaseName,
                                                 long lockAcquiredForMinutes,
                                                 long maxWaitingForLockMinutes,
                                                 int maxTries) {
    return new MongoSync4Driver(mongoClient, databaseName, lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries);
  }

  // For children classes like SpringData drivers
  protected MongoSync4Driver(MongoDatabase mongoDatabase,
                             long lockAcquiredForMinutes,
                             long maxWaitingForLockMinutes,
                             int maxTries) {
    super(mongoDatabase, lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries);
  }

  protected MongoSync4Driver(MongoClient mongoClient,
                             String databaseName,
                             long lockAcquiredForMinutes,
                             long maxWaitingForLockMinutes,
                             int maxTries) {
    super(mongoClient, databaseName, lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries);
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

  @Override
  public Class getLegacyMigrationChangeLogClass(boolean runAlways) {
    return runAlways ? MongockSync4LegacyMigrationChangeRunAlwaysLog.class : MongockSync4LegacyMigrationChangeLog.class;
  }
}
