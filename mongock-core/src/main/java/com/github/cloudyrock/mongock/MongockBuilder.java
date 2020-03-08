package com.github.cloudyrock.mongock;

import com.mongodb.client.MongoDatabase;
import io.changock.driver.mongo.v3.core.driver.ChangockMongoDriver;
import io.changock.runner.standalone.StandaloneChangockRunner;

public class MongockBuilder extends MongockBuilderBase<MongockBuilder, Mongock> {


  //Mandatory
  private final com.mongodb.MongoClient legacyMongoClient;
  private final com.mongodb.client.MongoClient mongoClient;
  private final String databaseName;

  /**
   * <p>Builder constructor takes the new API MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see com.mongodb.MongoClient docs
   * </p>
   *
   * @param newMongoClient        database connection client
   * @param databaseName          database name
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see com.mongodb.client.MongoClient
   */
  public MongockBuilder(com.mongodb.client.MongoClient newMongoClient, String databaseName, String changeLogsScanPackage) {
    this(newMongoClient, null, databaseName, changeLogsScanPackage);
  }

  /**
   * <p>Builder constructor takes db.mongodb.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see om.mongodb.client.MongoClient docs
   * </p>
   *
   * @param legacyMongoClient     database connection client
   * @param databaseName          database name
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see com.mongodb.MongoClient
   */
  @Deprecated
  public MongockBuilder(com.mongodb.MongoClient legacyMongoClient, String databaseName, String changeLogsScanPackage) {
    this(null, legacyMongoClient, databaseName, changeLogsScanPackage);
  }

  private MongockBuilder(com.mongodb.client.MongoClient mongoClient,
                         com.mongodb.MongoClient legacyMongoClient,
                         String databaseName,
                         String changeLogsScanPackage) {
    super(changeLogsScanPackage);
    this.mongoClient = mongoClient;
    this.legacyMongoClient = legacyMongoClient;
    this.databaseName = databaseName;
  }


  public Mongock build() {

    ChangockMongoDriver driver = new ChangockMongoDriver(getMongoDatabase())
        .setChangeLogCollectionName(changeLogCollectionName)
        .setLockCollectionName(lockCollectionName);

    StandaloneChangockRunner runner = StandaloneChangockRunner.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(changeLogsScanPackage)
        .setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock)
        .setLockConfig(lockAcquiredForMinutes, maxWaitingForLockMinutes, maxTries)
        .setEnabled(enabled)
        .setStartSystemVersion(startSystemVersion)
        .setEndSystemVersion(endSystemVersion)
        .withMetadata(metadata)
        .overrideAnnoatationProcessor(new MongockAnnotationProcessor())
        .build();
    return new Mongock(runner);
  }

  @Override
  protected MongockBuilder getInstance() {
    return this;
  }

  MongoDatabase getMongoDatabase() {
    return mongoClient != null ? mongoClient.getDatabase(databaseName) : legacyMongoClient.getDatabase(databaseName);
  }

}
