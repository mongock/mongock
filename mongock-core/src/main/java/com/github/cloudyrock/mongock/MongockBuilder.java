package com.github.cloudyrock.mongock;

import com.mongodb.MongoClient;

public class MongockBuilder extends MongockBuilderBase<MongockBuilder, Mongock> {


  public MongockBuilder(MongoClient mongoClient, String databaseName, String changeLogsScanPackage) {
    super(mongoClient, databaseName, changeLogsScanPackage);
  }

  @Override
  protected MongockBuilder returnInstance() {
    return this;
  }

  @Override
  Mongock createBuild() {

    Mongock mongock = new Mongock(changeEntryRepository, mongoClient, createChangeService(), lockChecker);
    mongock.setChangelogMongoDatabase(createMongoDataBaseProxy());
    mongock.setChangelogDb(createDbProxy());
    mongock.setConcreteChangeLogs(concreteChangeLogs);
    mongock.setEnabled(enabled);
    mongock.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    return mongock;
  }

}
