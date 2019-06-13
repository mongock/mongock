package com.github.cloudyrock.mongock;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Factory for {@link SpringBootMongock}
 */
public class SpringBootMongockBuilder extends MongockBuilderBase<SpringBootMongockBuilder, SpringBootMongock> {
  private ApplicationContext context;

  private static final Logger logger = LoggerFactory.getLogger(SpringMongockBuilder.class);

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
  public SpringBootMongockBuilder(MongoClient mongoClient, String databaseName, String changeLogsScanPackage) {
    super(mongoClient, databaseName, changeLogsScanPackage);
  }

  @Override
  protected SpringBootMongockBuilder returnInstance() {
    return this;
  }

  public SpringBootMongockBuilder setApplicationContext(ApplicationContext context) {
    this.context = context;
    return this;
  }

  @Override
  SpringBootMongock createBuild() {
    SpringBootMongock mongock = new SpringBootMongock(changeEntryRepository, mongoClient, createChangeService(), lockChecker);
    mongock.setChangelogMongoDatabase(createMongoDataBaseProxy());
    mongock.setChangelogDb(createDbProxy());
    mongock.setEnabled(enabled);
    mongock.springContext(context);
    mongock.setMongoTemplate(createMongoTemplateFromContext());
    mongock.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    return mongock;
  }

  private MongoTemplate createMongoTemplateFromContext() {
    MongoTemplate mongoTemplate;
    try {
      mongoTemplate = context.getBean(MongoTemplate.class);
    } catch (BeansException ex) {
      mongoTemplate = new MongoTemplate(mongoClient, databaseName);
    }
    return proxyFactory.createProxyFromOriginal(mongoTemplate, MongoTemplate.class);
  }

  @Override
  MongoDatabase createMongoDataBaseProxy() {
    try {
      return proxyFactory.createProxyFromOriginal(context.getBean(MongoDatabase.class), MongoDatabase.class);
    } catch (BeansException ex) {
      return proxyFactory.createProxyFromOriginal(database, MongoDatabase.class);
    }
  }

  @Override
  DB createDbProxy() {
    try {
      return proxyFactory.createProxyFromOriginal(context.getBean(DB.class), DB.class);
    } catch (BeansException ex) {
      return proxyFactory.createProxyFromOriginal(db, DB.class);
    }
  }

  @Override
  ChangeService createChangeService() {
    SpringChangeService changeService = new SpringChangeService();
    changeService.setEnvironment(context.getBean(Environment.class));
    changeService.setChangeLogsBasePackage(changeLogsScanPackage);
    return changeService;
  }

  @Override
  void validateMandatoryFields() throws MongockException {
    super.validateMandatoryFields();
    if (context == null) {
      throw new MongockException("ApplicationContext must be set to use SpringBootMongockBuilder");
    }
  }

}
