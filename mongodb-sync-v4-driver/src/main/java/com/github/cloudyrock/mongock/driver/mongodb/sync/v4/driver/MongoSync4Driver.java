package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.driver;

import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntryService;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.changelogs.runalways.MongockSync4LegacyMigrationChangeRunAlwaysLog;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.changelogs.runonce.MongockSync4LegacyMigrationChangeLog;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.repository.MongoSync4ChangeEntryRepository;
import com.github.cloudyrock.mongock.utils.TimeService;
import com.github.cloudyrock.mongock.utils.annotation.NotThreadSafe;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

@NotThreadSafe
public class MongoSync4Driver extends MongoSync4DriverBase<ChangeEntry> {

  protected MongoSync4ChangeEntryRepository<ChangeEntry> changeEntryRepository;

  private static final TimeService TIME_SERVICE = new TimeService();


  //TODO CENTRALIZE DEFAULT PROPERTIES
  public static MongoSync4Driver withDefaultLock(MongoClient mongoClient, String databaseName) {
    return MongoSync4Driver.withLockStrategy(mongoClient, databaseName, 60 * 1000L, 3 * 60 * 1000L, 1000L);
  }

  public static MongoSync4Driver withLockStrategy(MongoClient mongoClient,
                                                 String databaseName,
                                                 long lockAcquiredForMillis,
                                                 long lockQuitTryingAfterMillis,
                                                 long lockTryFrequencyMillis) {
    return new MongoSync4Driver(mongoClient, databaseName, lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
  }


  /**
   * @Deprecated
   * Use withLockStrategy instead
   */
  @Deprecated
  public static MongoSync4Driver withLockSetting(MongoClient mongoClient,
                                                 String databaseName,
                                                 long lockAcquiredForMinutes,
                                                 long maxWaitingForLockMinutes,
                                                 int maxTries) {
    long lockAcquiredForMillis = TIME_SERVICE.minutesToMillis(lockAcquiredForMinutes);
    long lockQuitTryingAfterMillis = TIME_SERVICE.minutesToMillis(maxWaitingForLockMinutes * maxTries);
    long tryFrequency = 1000L;// 1 second
    return MongoSync4Driver.withLockStrategy(mongoClient, databaseName, lockAcquiredForMillis, lockQuitTryingAfterMillis, tryFrequency);
  }

  // For children classes like SpringData drivers
  protected MongoSync4Driver(MongoDatabase mongoDatabase,
                             long lockAcquiredForMillis,
                             long lockQuitTryingAfterMillis,
                             long lockTryFrequencyMillis) {
    super(mongoDatabase, lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
  }

  protected MongoSync4Driver(MongoClient mongoClient,
                             String databaseName,
                             long lockAcquiredForMillis,
                             long lockQuitTryingAfterMillis,
                             long lockTryFrequencyMillis) {
    super(mongoClient, databaseName, lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
  }

  @Override
  public ChangeEntryService<ChangeEntry> getChangeEntryService() {
    if (changeEntryRepository == null) {
      this.changeEntryRepository = new MongoSync4ChangeEntryRepository<>(mongoDatabase.getCollection(changeLogCollectionName), indexCreation, getReadWriteConfiguration());
    }
    return changeEntryRepository;
  }

  @Override
  public Class getLegacyMigrationChangeLogClass(boolean runAlways) {
    return runAlways ? MongockSync4LegacyMigrationChangeRunAlwaysLog.class : MongockSync4LegacyMigrationChangeLog.class;
  }
}
