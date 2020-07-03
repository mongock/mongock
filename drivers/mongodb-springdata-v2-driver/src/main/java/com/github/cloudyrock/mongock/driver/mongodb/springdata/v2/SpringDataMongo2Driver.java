package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.impl.MongockTemplate;
import com.github.cloudyrock.mongock.driver.mongodb.v3.driver.MongoCore3Driver;
import io.changock.driver.api.driver.ChangeSetDependency;
import io.changock.driver.api.driver.ForbiddenParametersMap;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvokerImpl;
import io.changock.migration.api.exception.ChangockException;
import io.changock.utils.annotation.NotThreadSafe;
import org.springframework.data.mongodb.core.MongoTemplate;

@NotThreadSafe
public class SpringDataMongo2Driver extends MongoCore3Driver {

  private static final ForbiddenParametersMap FORBIDDEN_PARAMETERS_MAP;

  private final MongoTemplate mongoTemplate;

  static {
    FORBIDDEN_PARAMETERS_MAP = new ForbiddenParametersMap();
    FORBIDDEN_PARAMETERS_MAP.put(MongoTemplate.class, MongockTemplate.class);
  }

  public static SpringDataMongo2Driver withDefaultLock(MongoTemplate mongoTemplate) {
    return new SpringDataMongo2Driver(mongoTemplate, 3L, 4L, 3);
  }

  public static SpringDataMongo2Driver withLockSetting(MongoTemplate mongoTemplate,
                                                       long lockAcquiredForMinutes,
                                                       long maxWaitingForLockMinutes,
                                                       int maxTries) {
    return new SpringDataMongo2Driver(mongoTemplate, lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries);
  }

  public SpringDataMongo2Driver(MongoTemplate mongoTemplate,
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
  public void initialize() {
    super.initialize();
    if (!doesDependenciesContainMongockTemplate()) {
      dependencies.add(new ChangeSetDependency(MongockTemplate.class, new MongockTemplate(mongoTemplate, new LockGuardInvokerImpl(this.getLockManager()))));
    }
  }


  private boolean doesDependenciesContainMongockTemplate() {
    return dependencies != null && dependencies.stream().anyMatch(dependency -> MongockTemplate.class.isAssignableFrom(dependency.getType()));
  }

  @Override
  public ForbiddenParametersMap getForbiddenParameters() {
    return FORBIDDEN_PARAMETERS_MAP;
  }


}
