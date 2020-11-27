package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2;

import com.github.cloudyrock.mongock.migration.MongoDbLegacyMigration;
import io.changock.migration.api.config.LegacyMigrationMappingFields;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Deprecated use of properties prefixed with "mongock". Use prefix "changock" instead
 *
 * @see SpringDataMongoV2Configuration
 */
@Deprecated
@Configuration
@ConfigurationProperties("mongock")
public class MongockSpringDataV2Configuration extends SpringDataMongoV2Configuration {

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


  private MongockLegacyMigrationConfig legacyMigration = null;

  @Override
  @SuppressWarnings("unchecked")
  public MongoDbLegacyMigration getLegacyMigration() {
    return legacyMigration;
  }

  public void setLegacyMigration(MongockLegacyMigrationConfig legacyMigration) {
    this.legacyMigration = legacyMigration;
  }

  public static boolean isLegacyMigrationValid(MongockSpringDataV2Configuration config) {
    return config.getLegacyMigration() == null
        || StringUtils.isEmpty(config.getLegacyMigration().getCollectionName())
        || config.getLegacyMigration().getMappingFields() == null
        || StringUtils.isEmpty(config.getLegacyMigration().getMappingFields().getChangeId())
        || StringUtils.isEmpty(config.getLegacyMigration().getMappingFields().getAuthor());
  }

  public static class MongockLegacyMigrationConfig extends MongoDbLegacyMigration {

    @Override
    @ConfigurationProperties("mongock.legacy-migration.mapping-fields")
    public LegacyMigrationMappingFields getMappingFields() {
      return super.getMappingFields();
    }
  }
}
