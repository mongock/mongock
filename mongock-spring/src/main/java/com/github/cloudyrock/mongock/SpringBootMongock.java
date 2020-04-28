package com.github.cloudyrock.mongock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.Closeable;
import java.util.Optional;
import java.util.function.Supplier;

public class SpringBootMongock extends Mongock implements ApplicationRunner {


  private static final Logger logger = LoggerFactory.getLogger(SpringBootMongock.class);
  private ApplicationContext springContext;

  @Deprecated
  private MongoTemplate mongoTemplateProxy;
  @Deprecated
  private Supplier<MongoTemplate> mongoTemplateSupplier;

  SpringBootMongock(ChangeEntryRepository changeEntryRepository,ChangeService changeService, LockChecker lockChecker) {
    super(changeEntryRepository, changeService, lockChecker);
  }

  /**
   * @see ApplicationRunner#run(ApplicationArguments)
   * @see Mongock#execute()
   */
  @Override
  public void run(ApplicationArguments args) {
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
    } else if (springContext != null){
      return Optional.of(springContext.getBean(dependencyType));
    } else {
      return Optional.empty();
    }
  }

  SpringBootMongock springContext(ApplicationContext springContext) {
    this.springContext = springContext;
    return this;
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
