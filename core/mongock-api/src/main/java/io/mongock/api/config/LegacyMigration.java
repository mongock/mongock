package io.mongock.api.config;

import io.changock.migration.api.annotations.NonLockGuarded;
import io.changock.migration.api.annotations.NonLockGuardedType;

@NonLockGuarded(NonLockGuardedType.NONE)
public class LegacyMigration {

  private String origin;

  private boolean failFast = true;

  private Integer changesCountExpectation = null;

  private LegacyMigrationMappingFields mappingFields = new LegacyMigrationMappingFields();

  private boolean runAlways = false;


  public LegacyMigration() {
  }

  public LegacyMigration(String origin) {
    setOrigin(origin);
  }

  public LegacyMigration(String origin,
                         boolean failFast,
                         String changeId,
                         String author,
                         String timestamp,
                         String changeLogClass,
                         String changeSetMethod) {

    this(origin, failFast, changeId, author, timestamp, changeLogClass, changeSetMethod, null, null);
  }

  public LegacyMigration(String origin,
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
    setChangesCountExpectation(changesCountExpectation);
    setMappingFields(new LegacyMigrationMappingFields(changeId, author, timestamp, changeLogClass, changeSetMethod, metadata));
  }

  public LegacyMigrationMappingFields getMappingFields() {
    return mappingFields;
  }

  public void setMappingFields(LegacyMigrationMappingFields mappingFields) {
    this.mappingFields = mappingFields;
  }

  public Integer getChangesCountExpectation() {
    return changesCountExpectation;
  }

  public void setChangesCountExpectation(Integer changesCountExpectation) {
    this.changesCountExpectation = changesCountExpectation;
  }

  public boolean isRunAlways() {
    return runAlways;
  }

  public void setRunAlways(boolean runAlways) {
    this.runAlways = runAlways;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public boolean isFailFast() {
    return failFast;
  }

  public void setFailFast(boolean failFast) {
    this.failFast = failFast;
  }

  //TODO remove this legacy methods

  /**
   * @see LegacyMigration#setOrigin(String)
   */
  @Deprecated
  public String getCollectionName() {
    return origin;
  }

  /**
   * @see LegacyMigration#getOrigin()
   */
  @Deprecated
  public void setCollectionName(String collectionName) {
    this.origin = collectionName;
  }
}
