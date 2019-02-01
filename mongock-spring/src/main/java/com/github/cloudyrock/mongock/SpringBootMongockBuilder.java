package com.github.cloudyrock.mongock;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * Factory for {@link SpringBootMongock}
 */
public class SpringBootMongockBuilder extends MongockBuilderBase<SpringBootMongockBuilder, SpringBootMongock> {
  private ApplicationContext context;

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


    SpringChangeService changeService = new SpringChangeService();
    changeService.setEnvironment(context.getBean(Environment.class));
    changeService.setChangeLogsBasePackage(changeLogsScanPackage);
    SpringBootMongock mongock = new SpringBootMongock(changeEntryRepository, mongoClient, changeService, lockChecker);
    mongock.setChangelogMongoDatabase(proxyFactory.createProxyFromOriginal(mongoClient.getDatabase(databaseName), MongoDatabase.class));
    mongock.setChangelogDb(proxyFactory.createProxyFromOriginal(db, DB.class));
    mongock.setEnabled(enabled);
    mongock.springContext(context);
    mongock.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    return mongock;
  }

  @Override
  void validateMandatoryFields() throws MongockException {
    super.validateMandatoryFields();
    if (context == null) {
      throw new MongockException("ApplicationContext must be set to use SpringBootMongockBuilder");
    }
  }

}
