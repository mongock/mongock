package com.github.cloudyrock.spring.v5;

import io.changock.migration.api.exception.ChangockException;
import io.changock.runner.core.builder.configuration.LegacyMigration;
import io.changock.runner.core.builder.configuration.LegacyMigrationMappingFields;
import io.changock.runner.spring.util.config.ChangockSpringConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;


@Configuration
@ConfigurationProperties("spring.mongock")
public class MongockConfiguration extends ChangockSpringConfiguration {

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
  @SuppressWarnings("unchecked")
  public MongockLegacyMigration getLegacyMigration() {
    return legacyMigration;
  }

  public void setLegacyMigration(MongockLegacyMigration legacyMigration) {
    this.legacyMigration = legacyMigration;
  }

  public static boolean isLegacyMigrationValid(MongockConfiguration config) {
    return config.getLegacyMigration() == null
        || StringUtils.isEmpty(config.getLegacyMigration().getCollectionName())
        || config.getLegacyMigration().getMappingFields() == null
        || StringUtils.isEmpty(config.getLegacyMigration().getMappingFields().getChangeId())
        || StringUtils.isEmpty(config.getLegacyMigration().getMappingFields().getAuthor());
  }

  public static class MongockLegacyMigration extends LegacyMigration {

    private String collectionName;

    public MongockLegacyMigration() {
    }

    public MongockLegacyMigration(String collectionName) {
      if(collectionName == null || collectionName.isEmpty()) {
        throw new ChangockException("Legacy migration collectionName cannot be empty");
      }
      this.collectionName = collectionName;
    }


    public MongockLegacyMigration(String collectionName,
                                  LegacyMigrationMappingFields legacyMigrationMappingFields) {
      this.collectionName = collectionName;
      this.setMappingFields(legacyMigrationMappingFields);
    }


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
