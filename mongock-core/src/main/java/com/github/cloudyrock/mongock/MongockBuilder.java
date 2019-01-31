package com.github.cloudyrock.mongock;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongockBuilder extends AMongockBuilder implements IMongockBuilder {

  /**
   * <p>Builder constructor takes db.mongodb.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about <tt>MongoClient</tt> please see com.mongodb.MongoClient docs
   * </p>
   * @param mongoClient database connection client
   * @param databaseName database name
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see MongoClient
   */
  public MongockBuilder(MongoClient mongoClient, String databaseName, String changeLogsScanPackage) {
    super(mongoClient, databaseName, changeLogsScanPackage);
  }

  public Mongock constructMongock(ChangeEntryRepository changeEntryRepository, ChangeService changeService, LockChecker lockChecker,
      MongoDatabase mongoDatabaseProxy, DB db, ProxyFactory proxyFactory) {

    DB dbProxy = proxyFactory.createProxyFromOriginal(db, DB.class);
    Mongock mongock = new Mongock(changeEntryRepository, mongoClient,  changeService, lockChecker);
    mongock.setChangelogMongoDatabase(mongoDatabaseProxy);
    mongock.setChangelogDb(dbProxy);
    mongock.setEnabled(enabled);
    mongock.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);

    return mongock;
  }
}
