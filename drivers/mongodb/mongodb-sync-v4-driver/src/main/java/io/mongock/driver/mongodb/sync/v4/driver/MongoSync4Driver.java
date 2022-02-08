package io.mongock.driver.mongodb.sync.v4.driver;

import io.mongock.utils.TimeService;
import io.mongock.utils.annotation.NotThreadSafe;
import com.mongodb.client.MongoClient;


import static io.mongock.utils.Constants.DEFAULT_LOCK_ACQUIRED_FOR_MILLIS;
import static io.mongock.utils.Constants.DEFAULT_QUIT_TRYING_AFTER_MILLIS;
import static io.mongock.utils.Constants.DEFAULT_TRY_FREQUENCY_MILLIS;

@NotThreadSafe
public class MongoSync4Driver extends MongoSync4DriverBase {

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
    return MongoSync4Driver.withLockStrategy(mongoClient, databaseName, DEFAULT_LOCK_ACQUIRED_FOR_MILLIS, DEFAULT_QUIT_TRYING_AFTER_MILLIS, DEFAULT_TRY_FREQUENCY_MILLIS);
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
