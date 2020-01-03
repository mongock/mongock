package com.github.cloudyrock.mongock;

import com.mongodb.MongoClient;

public class SpringMongockBuilder extends SpringBaseMongockBuilder<SpringMongockBuilder, SpringMongock> {


  /**
   * <p>Builder constructor takes db.mongodb.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see com.mongodb.MongoClient docs
   * </p>
   *
   * @param legacyMongoClient           database connection client
   * @param databaseName          database name
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see MongoClient
   */
  public SpringMongockBuilder(MongoClient legacyMongoClient, String databaseName, String changeLogsScanPackage) {
    super(legacyMongoClient, databaseName, changeLogsScanPackage);
  }

  /**
   * <p>Builder constructor takes new API com.mongodb.client.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see com.mongodb.client.MongoClient docs
   * </p>
   *
   * @param newMongoClient        database connection client
   * @param databaseName          database name
   * @param changeLogsScanPackage package path where the changeLogs are located
   * @see MongoClient
   */
  public SpringMongockBuilder(com.mongodb.client.MongoClient newMongoClient, String databaseName, String changeLogsScanPackage) {
    super(newMongoClient, databaseName, changeLogsScanPackage);
  }

  @Override
  protected SpringMongock createMongockInstance() {
    SpringMongock mongock = new SpringMongock(changeEntryRepository, getMongoClientCloseable(), createChangeService(), lockChecker);
    mongock.setMongoTemplate(createMongoTemplateProxy());
    return mongock;
  }


  @Override
  protected SpringMongockBuilder returnInstance() {
    return this;
  }
}
