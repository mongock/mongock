package com.github.cloudyrock.mongock.migration;

import io.changock.runner.core.builder.configuration.LegacyMigration;

public class MongockLegacyMigration extends LegacyMigration {

  private String collectionName;

  private boolean failFast = true;

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
