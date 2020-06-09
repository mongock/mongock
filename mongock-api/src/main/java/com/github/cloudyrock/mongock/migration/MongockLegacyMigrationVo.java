package com.github.cloudyrock.mongock.migration;

import io.changock.runner.core.builder.configuration.LegacyMigrationMappingFields;

public class MongockLegacyMigrationVo {

  private String collectionName;

  private LegacyMigrationMappingFields mappingFields;

  public static MongockLegacyMigrationVo emptyMigration() {
    return new MongockLegacyMigrationVo();
  }

  private MongockLegacyMigrationVo() {
  }
  public MongockLegacyMigrationVo(String collectionName,
                                  LegacyMigrationMappingFields mappingFields) {
    this.collectionName = collectionName;
    this.mappingFields = mappingFields;
  }

  public String getCollectionName() {
    return collectionName;
  }

  public void setCollectionName(String collectionName) {
    this.collectionName = collectionName;
  }

  public LegacyMigrationMappingFields getMappingFields() {
    return mappingFields;
  }

  public void setMappingFields(LegacyMigrationMappingFields mappingFields) {
    this.mappingFields = mappingFields;
  }
}
