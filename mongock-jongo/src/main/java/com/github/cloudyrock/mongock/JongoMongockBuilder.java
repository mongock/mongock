package com.github.cloudyrock.mongock;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.jongo.Jongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JongoMongockBuilder extends AMongockBuilder implements IMongockBuilder {
    private static final Logger log = LoggerFactory.getLogger(JongoMongockBuilder.class);

  protected Jongo jongo = null;

  /**
   * <p>Builder constructor takes db.mongodb.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about <tt>MongoClient</tt> please see com.mongodb.MongoClient docs
   * </p>
   *
   * @param mongoClient           database connection client
   * @param databaseName          database name
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see MongoClient
   */
  public JongoMongockBuilder(MongoClient mongoClient, String databaseName, String changeLogsScanPackage) {
    super(mongoClient, databaseName, changeLogsScanPackage);
  }

  /**
   * Sets pre-configured {@link Jongo} instance to use by the changelogs
   *
   * @param jongo {@link Jongo} instance
   * @return JongoMongockBuilder builder
   */
  public JongoMongockBuilder setJongo(Jongo jongo) {
    this.jongo = jongo;
    return this;
  }

  public JongoMongock constructMongock(ChangeEntryRepository changeEntryRepository, ChangeService changeService, LockChecker lockChecker,
      MongoDatabase mongoDatabaseProxy, DB db, ProxyFactory proxyFactory) {

    DB dbProxy = proxyFactory.createProxyFromOriginal(db, DB.class);

    Jongo jongoProxy = proxyFactory.createProxyFromOriginal(jongo != null ? jongo : new Jongo(db), Jongo.class);
    JongoMongock mongock = new JongoMongock(changeEntryRepository, mongoClient,  changeService, lockChecker);
    mongock.setChangelogMongoDatabase(mongoDatabaseProxy);
    mongock.setChangelogDb(dbProxy);
    mongock.setJongo(jongoProxy);
    mongock.setEnabled(enabled);
    mongock.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    return mongock;
  }
}
