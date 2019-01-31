package com.github.cloudyrock.mongock;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

public class SpringMongockBuilder extends AMongockBuilder implements IMongockBuilder {

  private Environment springEnvironment = null;
  private MongoTemplate mongoTemplate = null;

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
  public SpringMongockBuilder(MongoClient mongoClient, String databaseName, String changeLogsScanPackage) {
    super(mongoClient, databaseName, changeLogsScanPackage);
  }

  @Override
  public IMongock build() {
    this.validateMandatoryFields();
    return super.build();
  }

  /**
   * Sets pre-configured {@link MongoTemplate} instance to use by the changelogs
   *
   * @param mongoTemplate instance of the {@link MongoTemplate}
   * @return Mongock builder
   */
  public SpringMongockBuilder setMongoTemplate(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
    return this;
  }

  /**
   * Set Environment object for Spring Profiles (@Profile) integration
   *
   * @param springEnvironment org.springframework.core.env.Environment object to inject
   * @return Mongock builder
   * @see org.springframework.context.annotation.Profile
   */
  public SpringMongockBuilder setSpringEnvironment(Environment springEnvironment) {
    this.springEnvironment = springEnvironment;
    return this;
  }

  public SpringMongock constructMongock(ChangeEntryRepository changeEntryRepository, ChangeService changeService, LockChecker lockChecker,
      MongoDatabase mongoDatabaseProxy, DB db, ProxyFactory proxyFactory) {

    SpringChangeService springChangeService = new SpringChangeService(changeService);
    springChangeService.setEnvironment(springEnvironment);
    DB dbProxy = proxyFactory.createProxyFromOriginal(db, DB.class);
    SpringMongock mongock = new SpringMongock(changeEntryRepository, mongoClient,  changeService, lockChecker);
    mongock.setChangelogMongoDatabase(mongoDatabaseProxy);
    mongock.setChangelogDb(dbProxy);
    MongoTemplate mongoTemplateProxy = proxyFactory.createProxyFromOriginal(mongoTemplate != null ? mongoTemplate : new MongoTemplate(mongoClient, databaseName), MongoTemplate.class);
    mongock.setMongoTemplate(mongoTemplateProxy);
    mongock.setEnabled(enabled);
    mongock.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    return mongock;
  }

  @Override
  void validateMandatoryFields() throws MongockException {
    if (springEnvironment == null) {
      throw new MongockException("springEnvironment must be set to use SpringMongockBuilder");
    }
  }
}
