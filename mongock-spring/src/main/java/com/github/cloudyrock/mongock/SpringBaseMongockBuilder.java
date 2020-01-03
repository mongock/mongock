package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.decorator.impl.MongoTemplateDecoratorImpl;
import com.mongodb.MongoClient;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

abstract class SpringBaseMongockBuilder<BUILDER_TYPE extends SpringBaseMongockBuilder, MONGOCK_TYPE extends Mongock> extends MongockBuilderBase<BUILDER_TYPE, MONGOCK_TYPE> {

  private Environment springEnvironment = null;

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
  SpringBaseMongockBuilder(MongoClient legacyMongoClient, String databaseName, String changeLogsScanPackage) {
    super(legacyMongoClient, databaseName, changeLogsScanPackage);
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
  SpringBaseMongockBuilder(com.mongodb.client.MongoClient newMongoClient, String databaseName, String changeLogsScanPackage) {
    super(newMongoClient, databaseName, changeLogsScanPackage);
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


  @Override
  protected  final ChangeService createChangeServiceInstance() {
    SpringChangeService changeService = new SpringChangeService();
    changeService.setEnvironment(springEnvironment);
    return changeService;
  }

  protected final MongoTemplate createMongoTemplateProxy() {
    return mongoClient !=null
        ? new MongoTemplateDecoratorImpl(mongoClient, databaseName, methodInvoker)
        : new MongoTemplateDecoratorImpl(legacyMongoClient, databaseName, methodInvoker) ;
  }


}
