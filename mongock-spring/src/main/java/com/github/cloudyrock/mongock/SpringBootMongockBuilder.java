package com.github.cloudyrock.mongock;

import com.mongodb.client.MongoClient;
import io.changock.driver.mongo.springdata.v2.driver.ChangockSpringDataMongoDriver;
import io.changock.runner.spring.v5.ChangockSpringApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Factory for {@link SpringBootMongock}
 */
public class SpringBootMongockBuilder extends SpringMongockBuilderBase<SpringBootMongockBuilder> {

  private ApplicationContext springContext;

  /**
   * <p>Builder constructor takes db.mongodb.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see com.mongodb.MongoClient docs
   * </p>
   *
   * @param legacyMongoClient           database connection client
   * @param databaseName          database name
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see com.mongodb.MongoClient
   */
  @Deprecated
  public SpringBootMongockBuilder(com.mongodb.MongoClient legacyMongoClient, String databaseName, String changeLogsScanPackage) {
    super(legacyMongoClient, databaseName, changeLogsScanPackage);

  }

  /**
   * <p>Builder constructor takes new API com.mongodb.client.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see om.mongodb.client.MongoClient docs
   * </p>
   *
   * @param newMongoClient           database connection client
   * @param databaseName          database name
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see MongoClient
   */
  @Deprecated
  public SpringBootMongockBuilder(MongoClient newMongoClient, String databaseName, String changeLogsScanPackage) {
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
  public SpringBootMongockBuilder(MongoTemplate mongoTemplate, String changeLogsScanPackage) {
    super(mongoTemplate, changeLogsScanPackage);

  }

  @Override
  protected SpringBootMongockBuilder getInstance() {
    return this;
  }

  /**
   * Set the Springboot application springContext from which the dependencies will be retrieved
   * @param context Springboot application springContext
   * @return Mongock builder for fluent interface
   * @see org.springframework.context.ApplicationContext
   */
  public SpringBootMongockBuilder setApplicationContext(ApplicationContext context) {
    this.springContext = context;
    return this;
  }


  public SpringBootMongock build() {
    ChangockSpringDataMongoDriver driver = new ChangockSpringDataMongoDriver(this.mongoTemplate)
        .setChangeLogCollectionName(changeLogCollectionName)
        .setLockCollectionName(lockCollectionName);

    ChangockSpringApplicationRunner runner = ChangockSpringApplicationRunner.builderV2_0()
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
        .build();
    return new SpringBootMongock(runner);
  }


}
