package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.driver;

import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.utils.TimeService;
import com.github.cloudyrock.mongock.utils.annotation.NotThreadSafe;
import com.mongodb.client.MongoClient;

@NotThreadSafe
public class MongoSync4Driver extends MongoSync4DriverGeneric<ChangeEntry> {

  protected MongoSync4Driver(MongoClient mongoClient,
                             String databaseName,
                             long lockAcquiredForMillis,
                             long lockQuitTryingAfterMillis,
                             long lockTryFrequencyMillis) {
    super(mongoClient, databaseName, lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
  }

  ////////////////////////////////////////////////////////////
  //BUILDER METHODS
  ////////////////////////////////////////////////////////////


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
   * @deprecated Use withLockStrategy instead
   */
  @Deprecated
  public static MongoSync4Driver withLockSetting(MongoClient mongoClient,
                                                 String databaseName,
                                                 long lockAcquiredForMinutes,
                                                 long maxWaitingForLockMinutes,
                                                 int maxTries) {
    TimeService timeService = new TimeService();
    long lockAcquiredForMillis = timeService.minutesToMillis(lockAcquiredForMinutes);
    long lockQuitTryingAfterMillis = timeService.minutesToMillis(maxWaitingForLockMinutes * maxTries);
    long tryFrequency = 1000L;// 1 second
    return MongoSync4Driver.withLockStrategy(mongoClient, databaseName, lockAcquiredForMillis, lockQuitTryingAfterMillis, tryFrequency);
  }
}
