package com.github.cloudyrock.mongock;

import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Factory for {@link SpringBootMongock}
 */
public abstract class SpringMongockBuilderBase<BUILDER_TYPE extends MongockBuilderBase, MONGOCK_RUNNER extends MongockBase>
    extends MongockBuilderBase<BUILDER_TYPE, MONGOCK_RUNNER> {

  protected final MongoTemplate mongoTemplate;

  /**
   * <p>Builder constructor takes new API com.mongodb.client.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see om.mongodb.client.MongoClient docs
   * </p>
   *
   * @param mongoTemplate         mongoTemplate
   * @param changeLogsScanPackage package path where the changelogs are located
   */
  public SpringMongockBuilderBase(MongoTemplate mongoTemplate, String changeLogsScanPackage) {
    super(changeLogsScanPackage);
    this.mongoTemplate = mongoTemplate;
  }

}
