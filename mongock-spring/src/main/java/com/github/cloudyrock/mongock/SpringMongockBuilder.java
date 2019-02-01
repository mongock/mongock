package com.github.cloudyrock.mongock;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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


  @Override
  SpringMongock createBuild() {
    SpringChangeService changeService = new SpringChangeService();
    changeService.setEnvironment(springEnvironment);
    changeService.setChangeLogsBasePackage(changeLogsScanPackage);

    SpringMongock mongock = new SpringMongock(changeEntryRepository, mongoClient, changeService, lockChecker);
    mongock.setChangelogMongoDatabase(proxyFactory.createProxyFromOriginal(mongoClient.getDatabase(databaseName), MongoDatabase.class));
    mongock.setChangelogDb(proxyFactory.createProxyFromOriginal(db, DB.class));
    mongock.setMongoTemplate(proxyFactory.createProxyFromOriginal(mongoTemplate != null ? mongoTemplate : new MongoTemplate(mongoClient, databaseName), MongoTemplate.class));
    mongock.setEnabled(enabled);
    mongock.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    return mongock;
  }
}
