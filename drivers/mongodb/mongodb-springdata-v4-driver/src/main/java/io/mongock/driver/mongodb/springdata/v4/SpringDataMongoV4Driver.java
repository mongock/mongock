package io.mongock.driver.mongodb.springdata.v4;

import io.mongock.utils.TimeService;
import io.mongock.utils.annotation.NotThreadSafe;
import org.springframework.data.mongodb.core.MongoTemplate;

import static io.mongock.utils.Constants.DEFAULT_LOCK_ACQUIRED_FOR_MILLIS;
import static io.mongock.utils.Constants.DEFAULT_QUIT_TRYING_AFTER_MILLIS;
import static io.mongock.utils.Constants.DEFAULT_TRY_FREQUENCY_MILLIS;

@NotThreadSafe
public class SpringDataMongoV4Driver extends SpringDataMongoV4DriverBase<SpringDataMongoV4Driver> {

  protected SpringDataMongoV4Driver(MongoTemplate mongoTemplate,
                                    long lockAcquiredForMillis,
                                    long lockQuitTryingAfterMillis,
                                    long lockTryFrequencyMillis) {
    super(mongoTemplate, lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
  }

  ////////////////////////////////////////////////////////////
  //BUILDER METHODS
  ////////////////////////////////////////////////////////////


  public static SpringDataMongoV4Driver withDefaultLock(MongoTemplate mongoTemplate) {
    return SpringDataMongoV4Driver.withLockStrategy(mongoTemplate, DEFAULT_LOCK_ACQUIRED_FOR_MILLIS, DEFAULT_QUIT_TRYING_AFTER_MILLIS, DEFAULT_TRY_FREQUENCY_MILLIS);
  }

  public static SpringDataMongoV4Driver withLockStrategy(MongoTemplate mongoTemplate,
                                                         long lockAcquiredForMillis,
                                                         long lockQuitTryingAfterMillis,
                                                         long lockTryFrequencyMillis) {
    return new SpringDataMongoV4Driver(mongoTemplate, lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
  }

  /**
   * @deprecated Use withLockStrategy instead
   */
  @Deprecated
  public static SpringDataMongoV4Driver withLockSetting(MongoTemplate mongoTemplate,
                                                        long lockAcquiredForMinutes,
                                                        long maxWaitingForLockMinutes,
                                                        int maxTries) {
    TimeService timeService = new TimeService();
    return SpringDataMongoV4Driver.withLockStrategy(
        mongoTemplate,
        timeService.minutesToMillis(lockAcquiredForMinutes),
        timeService.minutesToMillis(maxWaitingForLockMinutes * maxTries),
        1000L);
  }

  @Override
  public SpringDataMongoV4Driver copy() {
    SpringDataMongoV4Driver driver = new SpringDataMongoV4Driver(mongoTemplate, lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
    driver.setIndexCreation(indexCreation);
    if(isTransactionable()) {
      driver.enableTransaction();
    } else {
      driver.disableTransaction();
    }
    return driver;  }
}
