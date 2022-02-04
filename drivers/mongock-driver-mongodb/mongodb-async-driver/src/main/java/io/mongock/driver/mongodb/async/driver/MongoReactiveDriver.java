package io.mongock.driver.mongodb.async.driver;

import com.mongodb.reactivestreams.client.MongoClient;
import io.mongock.utils.TimeService;
import io.mongock.utils.annotation.NotThreadSafe;

import static io.mongock.utils.Constants.DEFAULT_LOCK_ACQUIRED_FOR_MILLIS;
import static io.mongock.utils.Constants.DEFAULT_QUIT_TRYING_AFTER_MILLIS;
import static io.mongock.utils.Constants.DEFAULT_TRY_FREQUENCY_MILLIS;

@NotThreadSafe
public class MongoReactiveDriver extends MongoReactiveDriverBase {

  protected MongoReactiveDriver(MongoClient mongoClient,
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
  public static MongoReactiveDriver withDefaultLock(MongoClient mongoClient, String databaseName) {
    return MongoReactiveDriver.withLockStrategy(mongoClient, databaseName, DEFAULT_LOCK_ACQUIRED_FOR_MILLIS, DEFAULT_QUIT_TRYING_AFTER_MILLIS, DEFAULT_TRY_FREQUENCY_MILLIS);
  }

  public static MongoReactiveDriver withLockStrategy(MongoClient mongoClient,
                                                     String databaseName,
                                                     long lockAcquiredForMillis,
                                                     long lockQuitTryingAfterMillis,
                                                     long lockTryFrequencyMillis) {
    return new MongoReactiveDriver(mongoClient, databaseName, lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
  }


  /**
   * @deprecated Use withLockStrategy instead
   */
  @Deprecated
  public static MongoReactiveDriver withLockSetting(MongoClient mongoClient,
                                                    String databaseName,
                                                    long lockAcquiredForMinutes,
                                                    long maxWaitingForLockMinutes,
                                                    int maxTries) {
    TimeService timeService = new TimeService();
    long lockAcquiredForMillis = timeService.minutesToMillis(lockAcquiredForMinutes);
    long lockQuitTryingAfterMillis = timeService.minutesToMillis(maxWaitingForLockMinutes * maxTries);
    long tryFrequency = 1000L;// 1 second
    return MongoReactiveDriver.withLockStrategy(mongoClient, databaseName, lockAcquiredForMillis, lockQuitTryingAfterMillis, tryFrequency);
  }
}
