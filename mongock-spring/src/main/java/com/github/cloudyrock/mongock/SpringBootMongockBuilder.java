package com.github.cloudyrock.mongock;

import io.changock.driver.mongo.springdata.v3.driver.ChangockSpringDataMongo3Driver;
import io.changock.runner.spring.v5.ChangockSpring5Runner;
import io.changock.runner.spring.v5.ChangockSpringApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Factory for {@link SpringBootMongock}
 */
public class SpringBootMongockBuilder extends SpringMongockBuilderBase<SpringBootMongockBuilder, SpringBootMongock> {

  private ApplicationContext springContext;

  /**
   * <p>Builder constructor takes new API com.mongodb.client.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see om.mongodb.client.MongoClient docs
   * </p>
   *
   * @param mongoTemplate         mongoTemplate
   * @param changeLogsScanPackage package path where the changelogs are located
   */
  public SpringBootMongockBuilder(MongoTemplate mongoTemplate, String changeLogsScanPackage) {
    super(mongoTemplate, changeLogsScanPackage);

  }


  /**
   * Set the Springboot application springContext from which the dependencies will be retrieved
   *
   * @param context Springboot application springContext
   * @return Mongock builder for fluent interface
   * @see org.springframework.context.ApplicationContext
   */
  public SpringBootMongockBuilder setApplicationContext(ApplicationContext context) {
    this.springContext = context;
    return this;
  }


  public SpringBootMongock build() {
    ChangockSpringDataMongo3Driver driver = buildDriver();
    ChangockSpring5Runner.ChangockSpring5RunnerBuilder builder = getBuilder(driver);
    return new SpringBootMongock(builder.buildApplicationRunner());
  }


  private ChangockSpring5Runner.ChangockSpring5RunnerBuilder getBuilder(ChangockSpringDataMongo3Driver driver) {
    return ChangockSpring5Runner.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(changeLogsScanPackage)
        .setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock)
        .setLockConfig(lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries)
        .setEnabled(enabled)
        .setStartSystemVersion(startSystemVersion)
        .setEndSystemVersion(endSystemVersion)
        .withMetadata(metadata)
        .setSpringContext(springContext)
        .overrideAnnoatationProcessor(new MongockAnnotationProcessor());
  }

  private ChangockSpringDataMongo3Driver buildDriver() {
    ChangockSpringDataMongo3Driver driver = new ChangockSpringDataMongo3Driver(this.mongoTemplate);
    driver.setChangeLogCollectionName(changeLogCollectionName);
    driver.setLockCollectionName(lockCollectionName);
    return driver;
  }


  @Override
  protected SpringBootMongockBuilder getInstance() {
    return this;
  }
}
