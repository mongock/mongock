package com.github.cloudyrock.mongock.migration;


import io.changock.migration.api.config.LegacyMigration;
import io.changock.migration.api.config.LegacyMigrationMappingFields;


public class MongoDbLegacyMigration extends LegacyMigration {


  public MongoDbLegacyMigration() {
  }

  public MongoDbLegacyMigration(String collectionName) {
    setOrigin(collectionName);
  }

  public MongoDbLegacyMigration(String origin,
                                boolean failFast,
                                String changeId,
                                String author,
                                String timestamp,
                                String changeLogClass,
                                String changeSetMethod) {

    this(origin, failFast, changeId, author, timestamp, changeLogClass, changeSetMethod, null, null);
  }

  public MongoDbLegacyMigration(String origin,
                                boolean failFast,
                                String changeId,
                                String author,
                                String timestamp,
                                String changeLogClass,
                                String changeSetMethod,
                                String metadata,
                                Integer changesCountExpectation) {
    setOrigin(origin);
    setFailFast(failFast);
    this.setChangesCountExpectation(changesCountExpectation);
    this.setMappingFields(new LegacyMigrationMappingFields(changeId, author, timestamp, changeLogClass, changeSetMethod, metadata));
  }

  public String getCollectionName() {
    return getOrigin();
  }

  public void setCollectionName(String collectionName) {
    setOrigin(collectionName);
  }

}
