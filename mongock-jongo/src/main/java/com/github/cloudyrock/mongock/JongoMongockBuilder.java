package com.github.cloudyrock.mongock;

import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JongoMongockBuilder extends MongockBuilderBase<JongoMongockBuilder, JongoMongock> {
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

  @Override
  protected JongoMongockBuilder returnInstance() {
    return this;
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

  @Override
  JongoMongock createBuild() {
    JongoMongock mongock = new JongoMongock(changeEntryRepository, mongoClient, createChangeService(), lockChecker);
    mongock.setChangelogMongoDatabase(createMongoDataBaseProxy());
    mongock.setChangelogDb(createDbProxy());
    mongock.setJongo(proxyFactory.createProxyFromOriginal(jongo != null ? jongo : new Jongo(db), Jongo.class));
    mongock.setEnabled(enabled);
    mongock.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    return mongock;

  }

}
