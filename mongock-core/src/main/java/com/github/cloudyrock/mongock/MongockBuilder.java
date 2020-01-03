package com.github.cloudyrock.mongock;

import com.mongodb.MongoClient;

public class MongockBuilder extends MongockBuilderBase<MongockBuilder, Mongock> {

  public MongockBuilder(MongoClient mongoClient, String databaseName, String changeLogsScanPackage) {
    super(mongoClient, databaseName, changeLogsScanPackage);
  }

  public MongockBuilder(com.mongodb.client.MongoClient mongoClient, String databaseName, String changeLogsScanPackage) {
    super(mongoClient, databaseName, changeLogsScanPackage);
  }

  @Override
  protected MongockBuilder returnInstance() {
    return this;
  }

  @Override
  protected Mongock createMongockInstance() {
    return new Mongock(changeEntryRepository, getMongoClientCloseable(), createChangeService(), lockChecker);
  }

  @Override
  protected ChangeService createChangeServiceInstance() {
    return new ChangeService();
  }


}
