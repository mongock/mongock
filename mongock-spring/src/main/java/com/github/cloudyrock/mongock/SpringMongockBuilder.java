package com.github.cloudyrock.mongock;

import com.mongodb.client.MongoClient;
import io.changock.driver.mongo.springdata.v2.driver.ChangockSpringDataMongoDriver;
import io.changock.runner.spring.v5.ChangockSpringInitializingBeanRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

public class SpringMongockBuilder extends SpringMongockBuilderBase<SpringMongockBuilder, SpringMongock> {


  private ApplicationContext springContext;
  private Environment springEnvironment;

  /**
   * <p>Builder constructor takes db.mongodb.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see com.mongodb.MongoClient docs
   * </p>
   *
   * @param legacyMongoClient     database connection client
   * @param databaseName          database name
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see com.mongodb.MongoClient
   */
  @Deprecated
  public SpringMongockBuilder(com.mongodb.MongoClient legacyMongoClient, String databaseName, String changeLogsScanPackage) {
    super(legacyMongoClient, databaseName, changeLogsScanPackage);
  }

  /**
   * <p>Builder constructor takes new API com.mongodb.client.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see om.mongodb.client.MongoClient docs
   * </p>
   *
   * @param newMongoClient        database connection client
   * @param databaseName          database name
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see MongoClient
   */
  @Deprecated
  public SpringMongockBuilder(MongoClient newMongoClient, String databaseName, String changeLogsScanPackage) {
    super(newMongoClient, databaseName, changeLogsScanPackage);
  }

  /**
   * <p>Builder constructor takes new API com.mongodb.client.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see om.mongodb.client.MongoClient docs
   * </p>
   *
   * @param mongoTemplate         mongoTemplate
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see MongoClient
   */
  public SpringMongockBuilder(MongoTemplate mongoTemplate, String changeLogsScanPackage) {
    super(mongoTemplate, changeLogsScanPackage);

  }

  @Override
  protected SpringMongockBuilder getInstance() {
    return this;
  }


  /**
   * Set the Springboot application springContext from which the dependencies will be retrieved
   *
   * @param context Springboot application springContext
   * @return Mongock builder for fluent interface
   * @see org.springframework.context.ApplicationContext
   */
  public SpringMongockBuilder setApplicationContext(ApplicationContext context) {
    this.springContext = context;
    return this;
  }

  /**
   * Set Environment object for Spring Profiles (@Profile) integration
   *
   * @param springEnvironment org.springframework.core.env.Environment object to inject
   * @return Mongock builder for fluent interface
   * @see org.springframework.context.annotation.Profile
   * @deprecated it should retrieve the environment from ApplicationContext
   */
  @Deprecated
  public SpringMongockBuilder setSpringEnvironment(Environment springEnvironment) {
    this.springEnvironment = springEnvironment;
    return this;
  }

  public SpringMongock build() {
    ChangockSpringDataMongoDriver driver = new ChangockSpringDataMongoDriver(this.mongoTemplate)
        .setChangeLogCollectionName(changeLogCollectionName)
        .setLockCollectionName(lockCollectionName);

    ChangockSpringInitializingBeanRunner runner = ChangockSpringInitializingBeanRunner.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(changeLogsScanPackage)
        .setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock)
        .setLockConfig(lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries)
        .setEnabled(enabled)
        .setStartSystemVersion(startSystemVersion)
        .setEndSystemVersion(endSystemVersion)
        .withMetadata(metadata)
        .setSpringContext(springContext)
        .overrideAnnoatationProcessor(new MongockAnnotationProcessor())
        .buildInitializingBeanRunner();
    return new SpringMongock(runner);
  }


}
