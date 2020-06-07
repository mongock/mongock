package com.github.cloudyrock.spring.v5;

import io.changock.runner.core.builder.configuration.LegacyMigration;
import io.changock.runner.core.builder.configuration.LegacyMigrationMappingFields;
import io.changock.runner.spring.util.config.ChangockSpringConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties("spring.mongock")
public class MongockConfiguration extends ChangockSpringConfiguration<MongockConfiguration.MongockLegacyMigration> {

  public final static String DEFAULT_CHANGELOG_COLLECTION_NAME = "mongockChangeLog";
  public final static String DEFAULT_LOCK_COLLECTION_NAME = "mongockLock";

  /**
   * Collection name for changeLogs history
   */
  private String changeLogCollectionName = DEFAULT_CHANGELOG_COLLECTION_NAME;

  /**
   * Collection name for locking mechanism
   */
  private String lockCollectionName = DEFAULT_LOCK_COLLECTION_NAME;

  /**
   * If false, Mongock won't create the necessary index. However it will check that they are already
   * created, failing otherwise. Default true
   */
  private boolean indexCreation = true;

  private MongockLegacyMigration legacyMigration = null;

  public String getChangeLogCollectionName() {
    return changeLogCollectionName;
  }

  public void setChangeLogCollectionName(String changeLogCollectionName) {
    this.changeLogCollectionName = changeLogCollectionName;
  }

  public String getLockCollectionName() {
    return lockCollectionName;
  }

  public void setLockCollectionName(String lockCollectionName) {
    this.lockCollectionName = lockCollectionName;
  }

  public boolean isIndexCreation() {
    return indexCreation;
  }

  public void setIndexCreation(boolean indexCreation) {
    this.indexCreation = indexCreation;
  }

  @Override
  public MongockLegacyMigration getLegacyMigration() {
    return legacyMigration;
  }

  public void setLegacyMigration(MongockLegacyMigration legacyMigration) {
    this.legacyMigration = legacyMigration;
  }


  public static class MongockLegacyMigration extends LegacyMigration {

    private String collectionName;

    public String getCollectionName() {
      return collectionName;
    }

    public void setCollectionName(String collectionName) {
      this.collectionName = collectionName;
    }

    @Override
    @ConfigurationProperties("spring.mongock.legacy-migration.mapping-fields")
    public LegacyMigrationMappingFields getMappingFields() {
      return super.getMappingFields();
    }
  }
}
