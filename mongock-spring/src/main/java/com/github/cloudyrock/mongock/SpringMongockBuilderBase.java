package com.github.cloudyrock.mongock;

import com.mongodb.client.MongoClient;
import io.changock.migration.api.exception.ChangockException;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for {@link SpringBootMongock}
 */
public abstract class SpringMongockBuilderBase<BUILDER_TYPE extends MongockBuilderBaseNew, MONGOCK_RUNNER extends MongockBase>
    extends MongockBuilderBaseNew<BUILDER_TYPE, MONGOCK_RUNNER> {

  protected final MongoTemplate mongoTemplate;

  /**
   * <p>Builder constructor takes db.mongodb.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see com.mongodb.MongoClient docs
   * </p>
   *
   * @param legacyMongoClient     database connection client
   * @param databaseName          database name
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see com.mongodb.MongoClient
   */
  @Deprecated
  public SpringMongockBuilderBase(com.mongodb.MongoClient legacyMongoClient, String databaseName, String changeLogsScanPackage) {
    this(new MongoTemplate(legacyMongoClient, databaseName), changeLogsScanPackage);

  }

  /**
   * <p>Builder constructor takes new API com.mongodb.client.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see om.mongodb.client.MongoClient docs
   * </p>
   *
   * @param newMongoClient        database connection client
   * @param databaseName          database name
   * @param changeLogsScanPackage package path where the changelogs are located
   * @see MongoClient
   */
  @Deprecated
  public SpringMongockBuilderBase(MongoClient newMongoClient, String databaseName, String changeLogsScanPackage) {
    this(new MongoTemplate(newMongoClient, databaseName), changeLogsScanPackage);
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
  public SpringMongockBuilderBase(MongoTemplate mongoTemplate, String changeLogsScanPackage) {
    super(changeLogsScanPackage);
    this.mongoTemplate = mongoTemplate;
  }

}
