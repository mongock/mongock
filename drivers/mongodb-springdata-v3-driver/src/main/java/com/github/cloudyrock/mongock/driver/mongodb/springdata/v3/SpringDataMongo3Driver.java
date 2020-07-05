package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import io.changock.driver.api.driver.ChangeSetDependency;
import io.changock.driver.api.driver.ForbiddenParametersMap;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvokerImpl;
import io.changock.migration.api.exception.ChangockException;
import io.changock.utils.annotation.NotThreadSafe;
import org.springframework.data.mongodb.core.MongoTemplate;

@NotThreadSafe
public class SpringDataMongo3Driver extends MongoSync4Driver {

  private static final ForbiddenParametersMap FORBIDDEN_PARAMETERS_MAP;

  static {
    FORBIDDEN_PARAMETERS_MAP = new ForbiddenParametersMap();
    FORBIDDEN_PARAMETERS_MAP.put(MongoTemplate.class, MongockTemplate.class);
  }

  private final MongoTemplate mongoTemplate;

  public static SpringDataMongo3Driver withDefaultLock(MongoTemplate mongoTemplate) {
    return new SpringDataMongo3Driver(mongoTemplate, 3L, 4L, 3);
  }

  public static SpringDataMongo3Driver withLockSetting(MongoTemplate mongoTemplate,
                                                       long lockAcquiredForMinutes,
                                                       long maxWaitingForLockMinutes,
                                                       int maxTries) {
    return new SpringDataMongo3Driver(mongoTemplate, lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries);
  }

  public SpringDataMongo3Driver(MongoTemplate mongoTemplate,
                                long lockAcquiredForMinutes,
                                long maxWaitingForLockMinutes,
                                int maxTries) {
    super(mongoTemplate.getDb(), lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries);
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public void runValidation() throws ChangockException {
    super.runValidation();
    if (this.mongoTemplate == null) {
      throw new ChangockException("MongoTemplate must not be null");
    }
  }

  @Override
  public void specificInitialization() {
    super.specificInitialization();
    dependencies.add(new ChangeSetDependency(MongockTemplate.class, new MongockTemplate(mongoTemplate, new LockGuardInvokerImpl(this.getLockManager()))));
  }

  @Override
  public ForbiddenParametersMap getForbiddenParameters() {
    return FORBIDDEN_PARAMETERS_MAP;
  }

  public MongockTemplate getMongockTemplate() {
    if(!isInitialized()) {
      throw new ChangockException("Mongock Driver hasn't been initialized yet");
    }
    return dependencies
        .stream()
        .filter(dependency -> MongockTemplate.class.isAssignableFrom(dependency.getType()))
        .map(ChangeSetDependency::getInstance)
        .map(instance -> (MongockTemplate)instance)
        .findAny()
        .orElseThrow(() -> new ChangockException("Mongock Driver hasn't been initialized yet"));

  }
}
