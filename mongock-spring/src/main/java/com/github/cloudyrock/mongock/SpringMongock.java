package com.github.cloudyrock.mongock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.Closeable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class SpringMongock extends Mongock implements InitializingBean {
  private static final Logger logger = LoggerFactory.getLogger(SpringMongock.class);

  @Deprecated
  private MongoTemplate mongoTemplateProxy;
  @Deprecated
  private Supplier<MongoTemplate> mongoTemplateSupplier;

  SpringMongock(ChangeEntryRepository changeEntryRepository, ChangeService changeService, LockChecker lockChecker) {
    super(changeEntryRepository, changeService, lockChecker);
  }

  /**
   * For Spring users: executing mongock after bean is created in the Spring context
   */
  @Override
  public void afterPropertiesSet() {
    execute();
  }

  @Override
  protected Optional<Object> getDependency(Class dependencyType) {
    Optional<Object> dependencyFromParent = super.getDependency(dependencyType);
    if(dependencyFromParent.isPresent()) {
      return dependencyFromParent;
    } else if(MongoTemplate.class.isAssignableFrom(dependencyType)) {
      logger.warn("[DEPRECATION] Use MongockTemplate instead of MongoTemplate. MongoTemplate in changeLogs is deprecated(will be removed in next major release)");
      return Optional.ofNullable(getMongoTemplateProxy());
    } else {
      return Optional.empty();
    }
  }

  @Deprecated
  private synchronized MongoTemplate getMongoTemplateProxy() {
    if(mongoTemplateProxy == null ) {
      mongoTemplateProxy = mongoTemplateSupplier.get();
    }
    return mongoTemplateProxy;
  }

  @Deprecated
  void setMongoTemplateSupplier(Supplier<MongoTemplate> mongoTemplateSupplier) {
    this.mongoTemplateSupplier = mongoTemplateSupplier;
  }
}
