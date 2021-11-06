package io.mongock.driver.mongodb.v3.driver;

import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.utils.TimeService;
import io.mongock.utils.annotation.NotThreadSafe;
import com.mongodb.client.MongoClient;

@NotThreadSafe
public class MongoCore3Driver extends MongoCore3DriverBase {

  protected MongoCore3Driver(MongoClient mongoClient,
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
   * @deprecated Use withLockStrategy instead
   */
  @Deprecated
  public static MongoCore3Driver withLockSetting(MongoClient mongoClient,
                                                 String databaseName,
                                                 long lockAcquiredForMinutes,
                                                 long maxWaitingForLockMinutes,
                                                 int maxTries) {
    TimeService timeService = new TimeService();
    long lockAcquiredForMillis = timeService.minutesToMillis(lockAcquiredForMinutes);
    long lockQuitTryingAfterMillis = timeService.minutesToMillis(maxWaitingForLockMinutes * maxTries);
    long tryFrequency = 1000L;// 1 second
    return MongoCore3Driver.withLockStrategy(mongoClient, databaseName, lockAcquiredForMillis, lockQuitTryingAfterMillis, tryFrequency);
  }
}
