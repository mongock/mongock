package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.decorator.impl.MongoTemplateDecoratorImpl;
import com.mongodb.MongoClient;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

public class SpringMongockBuilder extends MongockBuilderBase<SpringMongockBuilder, SpringMongock> {

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
  protected SpringMongockBuilder returnInstance() {
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


  @Override
  SpringMongock createBuild() {
    SpringMongock mongock = new SpringMongock(changeEntryRepository, mongoClient, createChangeService(), lockChecker);
    mongock.setChangelogMongoDatabase(createMongoDataBaseProxy());
    mongock.setMongoTemplate(createMongoTemplateProxy());
    mongock.setEnabled(enabled);
    mongock.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    return mongock;
  }

  private MongoTemplate createMongoTemplateProxy() {
    return  new MongoTemplateDecoratorImpl(mongoClient, databaseName, methodInvoker);
  }


  @Override
  ChangeService createChangeService() {
    SpringChangeService changeService = new SpringChangeService();
    changeService.setEnvironment(springEnvironment);
    changeService.setChangeLogsBasePackage(changeLogsScanPackage);
    return changeService;
  }
}
