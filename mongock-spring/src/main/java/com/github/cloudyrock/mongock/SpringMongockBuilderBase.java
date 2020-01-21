package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.decorator.impl.MongoTemplateDecoratorImpl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import static com.github.cloudyrock.mongock.StringUtils.hasText;

abstract class SpringMongockBuilderBase<BUILDER_TYPE extends SpringMongockBuilderBase, MONGOCK_TYPE extends Mongock> extends MongockBuilderBase<BUILDER_TYPE, MONGOCK_TYPE> {

  private Environment springEnvironment = null;
  private final MongoTemplate template;

  /**
   * <p>Builder constructor takes db.mongodb.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see com.mongodb.MongoClient docs
   * </p>
   *
   * @param legacyMongoClient           database connection client
   * @param databaseName          database name
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see com.mongodb.MongoClient
   */
  @Deprecated
  SpringMongockBuilderBase(com.mongodb.MongoClient legacyMongoClient, String databaseName, String changeLogsScanPackage) {
    super(legacyMongoClient, databaseName, changeLogsScanPackage);
    this.template = null;
  }

  /**
   * <p>Builder constructor takes new API com.mongodb.client.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see com.mongodb.client.MongoClient docs
   * </p>
   *
   * @param newMongoClient        database connection client
   * @param databaseName          database name
   * @param changeLogsScanPackage package path where the changeLogs are located
   * @see MongoClient
   */
  SpringMongockBuilderBase(MongoClient newMongoClient, String databaseName, String changeLogsScanPackage) {
    super(newMongoClient, databaseName, changeLogsScanPackage);
    this.template = null;
  }

  /**
   * <p>Builder constructor takes new API com.mongodb.client.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see com.mongodb.client.MongoClient docs
   * </p>
   *
   * @param template              MongoTemplate
   * @param databaseName          database name
   * @param changeLogsScanPackage package path where the changeLogs are located
   * @see MongoClient
   */
  SpringMongockBuilderBase(MongoTemplate template, String changeLogsScanPackage) {
    super((MongoClient)null, template.getDb().getName(), changeLogsScanPackage);
    this.template = template;
  }

  @Override
  MongoDatabase getMongoDatabase() {
    if(template != null ) {
      return template.getDb();
    } else if(mongoClient !=null) {
      return mongoClient.getDatabase(databaseName);
    } else {
      return legacyMongoClient.getDatabase(databaseName);
    }
  }

  @Override
  void validateMandatoryFields() throws MongockException {
    if (legacyMongoClient == null && mongoClient == null && template == null) {
      throw new MongockException("MongoClient cannot be null");
    }
    if (!hasText(databaseName)) {
      throw new MongockException("DB name is not set. It should be defined in MongoDB URI or via setter");
    }
    if (!hasText(changeLogsScanPackage)) {
      throw new MongockException("Scan package for changelogs is not set: use appropriate setter");
    }
  }


  /**
   * Set Environment object for Spring Profiles (@Profile) integration
   *
   * @param springEnvironment org.springframework.core.env.Environment object to inject
   * @return Mongock builder for fluent interface
   * @see org.springframework.context.annotation.Profile
   */
  public BUILDER_TYPE setSpringEnvironment(Environment springEnvironment) {
    this.springEnvironment = springEnvironment;
    return returnInstance();
  }

  Environment getSpringEnvironment() {
    return this.springEnvironment;
  }


  @Override
  protected  final ChangeService createChangeServiceInstance() {
    SpringChangeService changeService = new SpringChangeService();
    changeService.setEnvironment(springEnvironment);
    return changeService;
  }

  final MongoTemplate createMongoTemplateProxy() {
    if(template != null ) {
      return new MongoTemplateDecoratorImpl(template.getMongoDbFactory(), template.getConverter(), methodInvoker);
    } else if(mongoClient !=null) {
      return new MongoTemplateDecoratorImpl(mongoClient, databaseName, methodInvoker);
    } else {
      return new MongoTemplateDecoratorImpl(legacyMongoClient, databaseName, methodInvoker) ;
    }
  }


}
