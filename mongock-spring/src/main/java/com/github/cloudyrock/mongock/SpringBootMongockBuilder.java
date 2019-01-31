package com.github.cloudyrock.mongock;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * Factory for {@link SpringBootMongock}
 */
public class SpringBootMongockBuilder extends AMongockBuilder implements IMongockBuilder {

  private ApplicationContext context;

  /**
   * <p>Builder constructor takes db.mongodb.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about <tt>MongoClient</tt> please see com.mongodb.MongoClient docs
   * </p>
   *
   * @param mongoClient database connection client
   * @param databaseName database name
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see MongoClient
   */
  public SpringBootMongockBuilder(MongoClient mongoClient, String databaseName, String changeLogsScanPackage) {
    super(mongoClient, databaseName, changeLogsScanPackage);
  }

  @Override
  public IMongock build() {
    this.validateMandatoryFields();
    return super.build();
  }

  public SpringBootMongockBuilder setApplicationContext(ApplicationContext context) {
    this.context = context;
    return this;
  }

  public SpringBootMongock constructMongock(ChangeEntryRepository changeEntryRepository, ChangeService changeService, LockChecker lockChecker,
      MongoDatabase mongoDatabaseProxy, DB db, ProxyFactory proxyFactory) {

    DB dbProxy = proxyFactory.createProxyFromOriginal(db, DB.class);

    SpringChangeService springChangeService = new SpringChangeService(changeService);
    springChangeService.setEnvironment(context.getBean(Environment.class));
    SpringBootMongock mongock = new SpringBootMongock(changeEntryRepository, mongoClient, changeService, lockChecker);

    mongock.setChangelogMongoDatabase(mongoDatabaseProxy);
    mongock.setChangelogDb(dbProxy);
    mongock.setEnabled(enabled);
    mongock.springContext(context);
    mongock.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    return mongock;
  }

  @Override
  void validateMandatoryFields() throws MongockException {
    if (context == null) {
      throw new MongockException("ApplicationContext must be set to use SpringBootMongockBuilder");
    }
  }
}
