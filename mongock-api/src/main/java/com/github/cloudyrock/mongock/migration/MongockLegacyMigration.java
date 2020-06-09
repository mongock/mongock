package com.github.cloudyrock.mongock.migration;

import io.changock.runner.core.builder.configuration.LegacyMigration;

public class MongockLegacyMigration extends LegacyMigration {

  private String collectionName;

  public String getCollectionName() {
    return collectionName;
  }

  public void setCollectionName(String collectionName) {
    this.collectionName = collectionName;
  }

}
