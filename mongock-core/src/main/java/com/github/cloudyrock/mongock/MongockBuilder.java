package com.github.cloudyrock.mongock;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

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
    ChangeService changeService = new ChangeService();
    changeService.setChangeLogsBasePackage(changeLogsScanPackage);
    Mongock mongock = new Mongock(changeEntryRepository, mongoClient, changeService, lockChecker);
    mongock.setChangelogMongoDatabase(proxyFactory.createProxyFromOriginal(mongoClient.getDatabase(databaseName), MongoDatabase.class));
    mongock.setChangelogDb(proxyFactory.createProxyFromOriginal(db, DB.class));
    mongock.setEnabled(enabled);
    mongock.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    return mongock;
  }

}
