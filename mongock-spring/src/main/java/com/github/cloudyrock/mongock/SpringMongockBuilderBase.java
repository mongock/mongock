package com.github.cloudyrock.mongock;

import com.mongodb.client.MongoClient;
import io.changock.driver.mongo.springdata.v2.driver.ChangockSpringDataMongoDriver;
import io.changock.migration.api.exception.ChangockException;
import io.changock.runner.spring.v5.ChangockSpringApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for {@link SpringBootMongock}
 */
public class SpringMongockBuilderBase {

  private final MongoTemplate mongoTemplate;
  private final String changeLogsScanPackage;
  private long lockAcquiredForMinutes = 24L * 60L;
  private long maxWaitingForLockMinutes = 3L;
  private int maxTries = 1;
  boolean throwExceptionIfCannotObtainLock = false;
  boolean enabled = true;
  private String changeLogCollectionName = "mongockChangeLog";
  private String lockCollectionName = "mongockLock";
  private String startSystemVersion = "0";
  private String endSystemVersion = String.valueOf(Integer.MAX_VALUE);
  private Map<String, Object> metadata = new HashMap<>();
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
  public SpringMongockBuilderBase(com.mongodb.MongoClient legacyMongoClient, String databaseName, String changeLogsScanPackage) {
    this(new MongoTemplate(legacyMongoClient, databaseName), changeLogsScanPackage);

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
  public SpringMongockBuilderBase(MongoClient newMongoClient, String databaseName, String changeLogsScanPackage) {
    this(new MongoTemplate(newMongoClient, databaseName), changeLogsScanPackage);
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
  public SpringMongockBuilderBase(MongoTemplate mongoTemplate, String changeLogsScanPackage) {
    this.mongoTemplate = mongoTemplate;
    this.changeLogsScanPackage = changeLogsScanPackage;
  }




  /**
   * Feature which enables/disables throwing MongockException if the lock cannot be obtained
   *
   * @param throwExceptionIfCannotObtainLock Mongock will throw MongockException if lock can not be obtained
   * @return Mongock builder for fluent interface
   */
  public SpringMongockBuilderBase setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
    this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
    return this;
  }


  /**
   * Feature which enables/disables execution
   *
   * @param enabled Migration process will run only if this option is set to true
   * @return Mongock builder for fluent interface
   */
  public SpringMongockBuilderBase setEnabled(boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  /**
   * Set up the lock with minimal configuration. This implies Mongock will throw an exception if cannot obtains the lock.
   *
   * @param lockAcquiredForMinutes   Acquired time in minutes
   * @param maxWaitingForLockMinutes max time in minutes to wait for the lock in each try.
   * @param maxTries                 number of tries
   * @return Mongock builder for fluent interface
   */
  public SpringMongockBuilderBase setLockConfig(long lockAcquiredForMinutes, long maxWaitingForLockMinutes, int maxTries) {
    this.lockAcquiredForMinutes = lockAcquiredForMinutes;
    this.maxWaitingForLockMinutes = maxWaitingForLockMinutes;
    this.maxTries = maxTries;
    this.throwExceptionIfCannotObtainLock = true;
    return this;
  }

  /**
   * Set up the lock with default configuration to wait for it and through an exception when cannot obtain it.
   *
   * @return Mongock builder for fluent interface
   */
  public SpringMongockBuilderBase setLockQuickConfig() {
    setLockConfig(3, 4, 3);
    return this;
  }

  public SpringMongockBuilderBase setChangeLogCollectionName(String changeLogCollectionName) {
    if(changeLogCollectionName == null || "".equals(changeLogCollectionName)) {
      throw new ChangockException("invalid changeLog collection name");
    }
    this.changeLogCollectionName = changeLogCollectionName;
    return this;
  }

  /**
   * Set up the start Version for versioned schema changes.
   * This shouldn't be confused with the changeSet systemVersion. This is from a consultancy point of view.
   * So the changeSet are tagged with a systemVersion and then when building Mongock, you specify
   * the systemVersion range you want to apply, so all the changeSets tagget with systemVersion inside that
   * range will be applied
   *
   * @param startSystemVersion Version to start with
   * @return Mongock builder for fluent interface
   */
  public SpringMongockBuilderBase setStartSystemVersion(String startSystemVersion) {
    this.startSystemVersion = startSystemVersion;
    return this;
  }

  /**
   * Set up the end Version for versioned schema changes.
   * This shouldn't be confused with the changeSet systemVersion. This is from a consultancy point of view.
   * So the changeSet are tagged with a systemVersion and then when building Mongock, you specify
   * the systemVersion range you want to apply, so all the changeSets tagget with systemVersion inside that
   * range will be applied
   *
   * @param endSystemVersion Version to end with
   * @return Mongock builder for fluent interface
   */
  public SpringMongockBuilderBase setEndSystemVersion(String endSystemVersion) {
    this.endSystemVersion = endSystemVersion;
    return this;
  }

  /**
   * Set the metadata for the mongock process. This metadata will be added to each document in the mongockChangeLog
   * collection. This is useful when the system needs to add some extra info to the changeLog.
   *
   * @param metadata Custom metadata object  to be added
   * @return Mongock builder for fluent interface
   */
  public SpringMongockBuilderBase withMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
    return this;
  }

  /**
   * Set the Springboot application springContext from which the dependencies will be retrieved
   * @param context Springboot application springContext
   * @return Mongock builder for fluent interface
   * @see ApplicationContext
   */
  public SpringMongockBuilderBase setApplicationContext(ApplicationContext context) {
    this.springContext = context;
    return this;
  }


  public ChangockSpringApplicationRunner build() {
    ChangockSpringDataMongoDriver driver = new ChangockSpringDataMongoDriver(this.mongoTemplate)
        .setChangeLogCollectionName(changeLogCollectionName)
        .setLockCollectionName(lockCollectionName);

    return ChangockSpringApplicationRunner.builderV2_0()
        .setDriver(driver)
        .addChangeLogsScanPackage(changeLogsScanPackage)
        .setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock)
        .setLockConfig(lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries)
        .setEnabled(enabled)
        .setStartSystemVersion(startSystemVersion)
        .setEndSystemVersion(endSystemVersion)
        .withMetadata(metadata)
        .setSpringContext(springContext)
        .build();
  }


}
