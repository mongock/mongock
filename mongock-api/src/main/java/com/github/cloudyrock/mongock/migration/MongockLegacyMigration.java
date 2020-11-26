package com.github.cloudyrock.mongock.migration;


import io.changock.migration.api.config.LegacyMigration;
import io.changock.migration.api.config.LegacyMigrationMappingFields;
import org.apache.commons.lang3.StringUtils;

public class MongockLegacyMigration extends LegacyMigration {

  private String collectionName;

  private boolean failFast = true;

  public MongockLegacyMigration() {
  }

  public MongockLegacyMigration(String collectionName) {
    this.collectionName = collectionName;
  }

  public MongockLegacyMigration(String collectionName,
                                boolean failFast,
                                String changeId,
                                String author,
                                String timestamp,
                                String changeLogClass,
                                String changeSetMethod) {

    this(collectionName, failFast, changeId, author, timestamp, changeLogClass, changeSetMethod, null, null);
    this.failFast = failFast;
  }

  public MongockLegacyMigration(String collectionName,
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
