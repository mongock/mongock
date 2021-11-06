package io.mongock.driver.mongodb.springdata.v2;

import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.utils.TimeService;
import io.mongock.utils.annotation.NotThreadSafe;
import org.springframework.data.mongodb.core.MongoTemplate;

@NotThreadSafe
public class SpringDataMongoV2Driver extends SpringDataMongoV2DriverBase {

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
    return SpringDataMongoV2Driver.withLockStrategy(mongoTemplate, 60 * 1000L, 3 * 60 * 1000L, 1000L);
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
}
