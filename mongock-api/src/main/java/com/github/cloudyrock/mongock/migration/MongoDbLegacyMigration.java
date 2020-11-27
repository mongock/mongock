package com.github.cloudyrock.mongock.migration;


import io.changock.migration.api.config.LegacyMigration;
import io.changock.migration.api.config.LegacyMigrationMappingFields;


public class MongoDbLegacyMigration extends LegacyMigration {

  private String collectionName;

  private boolean failFast = true;

  public MongoDbLegacyMigration() {
  }

  public MongoDbLegacyMigration(String collectionName) {
    this.collectionName = collectionName;
  }

  public MongoDbLegacyMigration(String collectionName,
                                boolean failFast,
                                String changeId,
                                String author,
                                String timestamp,
                                String changeLogClass,
                                String changeSetMethod) {

    this(collectionName, failFast, changeId, author, timestamp, changeLogClass, changeSetMethod, null, null);
    this.failFast = failFast;
  }

  public MongoDbLegacyMigration(String collectionName,
                                boolean failFast,
                                String changeId,
                                String author,
                                String timestamp,
                                String changeLogClass,
                                String changeSetMethod,
                                String metadata,
                                Integer changesCountExpectation) {
    this.collectionName = collectionName;
    this.failFast = failFast;
    this.setChangesCountExpectation(changesCountExpectation);
    this.setMappingFields(new LegacyMigrationMappingFields(changeId, author, timestamp, changeLogClass, changeSetMethod, metadata));
  }

  public String getCollectionName() {
    return collectionName;
  }

  public void setCollectionName(String collectionName) {
    this.collectionName = collectionName;
  }

  public boolean isFailFast() {
    return failFast;
  }

  public void setFailFast(boolean failFast) {
    this.failFast = failFast;
  }
}
