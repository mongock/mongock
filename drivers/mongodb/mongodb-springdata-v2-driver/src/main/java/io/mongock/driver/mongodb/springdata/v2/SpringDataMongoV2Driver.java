package io.mongock.driver.mongodb.springdata.v2;

import io.mongock.utils.TimeService;
import io.mongock.utils.annotation.NotThreadSafe;
import org.springframework.data.mongodb.core.MongoTemplate;

import static io.mongock.utils.Constants.DEFAULT_LOCK_ACQUIRED_FOR_MILLIS;
import static io.mongock.utils.Constants.DEFAULT_QUIT_TRYING_AFTER_MILLIS;
import static io.mongock.utils.Constants.DEFAULT_TRY_FREQUENCY_MILLIS;

@NotThreadSafe
public class SpringDataMongoV2Driver extends SpringDataMongoV2DriverBase<SpringDataMongoV2Driver> {

  protected SpringDataMongoV2Driver(MongoTemplate mongoTemplate,
                                    long lockAcquiredForMillis,
                                    long lockQuitTryingAfterMillis,
                                    long lockTryFrequencyMillis) {
    super(mongoTemplate, lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
  }

  ////////////////////////////////////////////////////////////
  //BUILDER METHODS
  ////////////////////////////////////////////////////////////


  public static SpringDataMongoV2Driver withDefaultLock(MongoTemplate mongoTemplate) {
    return SpringDataMongoV2Driver.withLockStrategy(mongoTemplate, DEFAULT_LOCK_ACQUIRED_FOR_MILLIS, DEFAULT_QUIT_TRYING_AFTER_MILLIS, DEFAULT_TRY_FREQUENCY_MILLIS);
  }

  public static SpringDataMongoV2Driver withLockStrategy(MongoTemplate mongoTemplate,
                                                         long lockAcquiredForMillis,
                                                         long lockQuitTryingAfterMillis,
                                                         long lockTryFrequencyMillis) {
    return new SpringDataMongoV2Driver(mongoTemplate, lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
  }

  /**
   * @deprecated Use withLockStrategy instead
   */
  @Deprecated
  public static SpringDataMongoV2Driver withLockSetting(MongoTemplate mongoTemplate,
                                                        long lockAcquiredForMinutes,
                                                        long maxWaitingForLockMinutes,
                                                        int maxTries) {
    TimeService timeService = new TimeService();
    return SpringDataMongoV2Driver.withLockStrategy(
        mongoTemplate,
        timeService.minutesToMillis(lockAcquiredForMinutes),
        timeService.minutesToMillis(maxWaitingForLockMinutes * maxTries),
        1000L);
  }

  @Override
  public SpringDataMongoV2Driver copy() {
    SpringDataMongoV2Driver driver = new SpringDataMongoV2Driver(mongoTemplate, lockAcquiredForMillis, lockQuitTryingAfterMillis, lockTryFrequencyMillis);
    driver.setIndexCreation(indexCreation);
    if(isTransactionable()) {
      driver.enableTransaction();
    } else {
      driver.disableTransaction();
    }
    return driver;
  }
}
