package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.decorator.impl.MongockTemplate;
import com.mongodb.client.MongoClient;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.function.Supplier;

public class SpringMongockBuilder extends SpringMongockBuilderBase<SpringMongockBuilder, SpringMongock> {


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
  public SpringMongockBuilder(com.mongodb.MongoClient legacyMongoClient, String databaseName, String changeLogsScanPackage) {
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
  @Deprecated
  public SpringMongockBuilder(MongoClient newMongoClient, String databaseName, String changeLogsScanPackage) {
    super(newMongoClient, databaseName, changeLogsScanPackage);
  }

  /**
   * <p>Builder constructor takes new API com.mongodb.client.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see om.mongodb.client.MongoClient docs
   * </p>
   *
   * @param mongoTemplate         mongoTemplate
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see MongoClient
   */
  public SpringMongockBuilder(MongoTemplate mongoTemplate, String changeLogsScanPackage) {
    super(mongoTemplate, changeLogsScanPackage);
  }

  @Override
  protected SpringMongock createMongockInstance() {
    SpringMongock mongock = new SpringMongock(changeEntryRepository, createChangeService(), lockChecker);
    mongock.addChangeSetDependency(Environment.class, this.getSpringEnvironment());
    mongock.addChangeSetDependency(MongockTemplate.class, getMongockTemplate());
    mongock.setMongoTemplateSupplier(getMongoTemplateProxySupplier());
    return mongock;
  }


  @Override
  protected SpringMongockBuilder returnInstance() {
    return this;
  }


}
