package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.decorator.impl.MongoTemplateDecoratorImpl;
import com.mongodb.MongoClient;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Factory for {@link SpringBootMongock}
 */
public class SpringBootMongockBuilder extends MongockBuilderBase<SpringBootMongockBuilder, SpringBootMongock> {
  private ApplicationContext context;

  /**
   * <p>Builder constructor takes db.mongodb.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see com.mongodb.MongoClient docs
   * </p>
   *
   * @param legacyMongoClient           database connection client
   * @param databaseName          database name
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see MongoClient
   */
  public SpringBootMongockBuilder(MongoClient legacyMongoClient, String databaseName, String changeLogsScanPackage) {
    super(legacyMongoClient, databaseName, changeLogsScanPackage);
  }

  /**
   * <p>Builder constructor takes new API com.mongodb.client.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see om.mongodb.client.MongoClient docs
   * </p>
   *
   * @param newMongoClient           database connection client
   * @param databaseName          database name
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see MongoClient
   */
  public SpringBootMongockBuilder(com.mongodb.client.MongoClient newMongoClient, String databaseName, String changeLogsScanPackage) {
    super(newMongoClient, databaseName, changeLogsScanPackage);
  }

  /**
   * Set the Springboot application context from which the dependencies will be retrieved
   * @param context Springboot application context
   * @return Mongock builder for fluent interface
   * @see org.springframework.context.ApplicationContext
   */
  public SpringBootMongockBuilder setApplicationContext(ApplicationContext context) {
    this.context = context;
    return this;
  }

  @Override
  protected SpringBootMongock createMongockInstance() {
    SpringBootMongock mongock = new SpringBootMongock(changeEntryRepository, getMongoClientCloseable(), createChangeService(), lockChecker);
    mongock.springContext(context);
    mongock.setMongoTemplate(createMongoTemplateProxy());
    return mongock;
  }

  @Override
  protected ChangeService createChangeServiceInstance() {
    SpringChangeService changeService = new SpringChangeService();
    changeService.setEnvironment(context.getBean(Environment.class));
    return changeService;
  }

  @Override
  void validateMandatoryFields() throws MongockException {
    super.validateMandatoryFields();
    if (context == null) {
      throw new MongockException("ApplicationContext must be set to use SpringBootMongockBuilder");
    }
  }


  private MongoTemplate createMongoTemplateProxy() {
    return mongoClient !=null
        ? new MongoTemplateDecoratorImpl(mongoClient, databaseName, methodInvoker)
        : new MongoTemplateDecoratorImpl(legacyMongoClient, databaseName, methodInvoker) ;
  }

  @Override
  protected SpringBootMongockBuilder returnInstance() {
    return this;
  }

}
