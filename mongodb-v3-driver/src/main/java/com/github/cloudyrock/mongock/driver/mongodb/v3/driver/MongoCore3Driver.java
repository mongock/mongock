package com.github.cloudyrock.mongock.driver.mongodb.v3.driver;

import com.github.cloudyrock.mongock.driver.api.driver.ForbiddenParametersMap;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntryService;
import com.github.cloudyrock.mongock.driver.mongodb.v3.changelogs.runalways.MongockV3LegacyMigrationChangeRunAlwaysLog;
import com.github.cloudyrock.mongock.driver.mongodb.v3.changelogs.runonce.MongockV3LegacyMigrationChangeLog;
import com.github.cloudyrock.mongock.driver.mongodb.v3.repository.Mongo3ChangeEntryRepository;
import com.github.cloudyrock.mongock.utils.TimeService;
import com.github.cloudyrock.mongock.utils.annotation.NotThreadSafe;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

@NotThreadSafe
public class MongoCore3Driver extends MongoCore3DriverBase<ChangeEntry> {

  private static final ForbiddenParametersMap FORBIDDEN_PARAMETERS_MAP = new ForbiddenParametersMap();

  protected Mongo3ChangeEntryRepository<ChangeEntry> changeEntryRepository;

  private static final TimeService TIME_SERVICE = new TimeService();


  //TODO CENTRALIZE DEFAULT PROPERTIES
  public static MongoCore3Driver withDefaultLock(MongoClient mongoClient, String databaseName) {
    return MongoCore3Driver.withLockStrategy(mongoClient, databaseName, 60 * 1000L, 3 * 60 * 1000L, 1000L);
  }

  public static MongoCore3Driver withLockStrategy(MongoClient mongoClient,
                                                  String databaseName,
                                                  long lockAcquiredForMillis,
                                                  long lockQuitTryingAfterMillis,
                                                  long lockTryFrequencyMillis) {
    return new MongoCore3Driver(mongoClient, databaseName, lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
  }


  /**
   * @Deprecated
   * Use withLockStrategy instead
   */
  @Deprecated
  public static MongoCore3Driver withLockSetting(MongoClient mongoClient,
                                                 String databaseName,
                                                 long lockAcquiredForMinutes,
                                                 long maxWaitingForLockMinutes,
                                                 int maxTries) {
    long lockAcquiredForMillis = TIME_SERVICE.minutesToMillis(lockAcquiredForMinutes);
    long lockQuitTryingAfterMillis = TIME_SERVICE.minutesToMillis(maxWaitingForLockMinutes * maxTries);
    long tryFrequency = 1000L;// 1 second
    return MongoCore3Driver.withLockStrategy(mongoClient, databaseName, lockAcquiredForMillis, lockQuitTryingAfterMillis, tryFrequency);
  }

  // For children classes like SpringData drivers
  protected MongoCore3Driver(MongoDatabase mongoDatabase,
                             long lockAcquiredForMillis,
                             long lockQuitTryingAfterMillis,
                             long lockTryFrequencyMillis) {
    super(mongoDatabase, lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
  }

  protected MongoCore3Driver(MongoClient mongoClient,
                             String databaseName,
                             long lockAcquiredForMillis,
                             long lockQuitTryingAfterMillis,
                             long lockTryFrequencyMillis) {
    super(mongoClient, databaseName, lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
  }

  @Override
  public ChangeEntryService<ChangeEntry> getChangeEntryService() {
    if (changeEntryRepository == null) {
      this.changeEntryRepository = new Mongo3ChangeEntryRepository<>(mongoDatabase.getCollection(changeLogCollectionName), indexCreation, getReadWriteConfiguration());
    }
    return changeEntryRepository;
  }

  @Override
  public ForbiddenParametersMap getForbiddenParameters() {
    return FORBIDDEN_PARAMETERS_MAP;
  }

  @Override
  public Class getLegacyMigrationChangeLogClass(boolean runAlways) {
    return runAlways ? MongockV3LegacyMigrationChangeRunAlwaysLog.class : MongockV3LegacyMigrationChangeLog.class;
  }
}
