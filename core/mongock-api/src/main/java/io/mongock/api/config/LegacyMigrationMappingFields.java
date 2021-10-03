package io.mongock.api.config;

import java.util.Objects;

public class LegacyMigrationMappingFields {

  private String changeId = "changeId";
  private String author = "author";
  private String timestamp = "timestamp";
  private String changeLogClass = "changeLogClass";
  private String changeSetMethod = "changeSetMethod";
  private String metadata = null;

  public LegacyMigrationMappingFields() {
  }

  public LegacyMigrationMappingFields(String changeId, String author, String timestamp, String changeLogClass, String changeSetMethod, String metadata) {
    this.changeId = changeId;
    this.author = author;
    this.timestamp = timestamp;
    this.changeLogClass = changeLogClass;
    this.changeSetMethod = changeSetMethod;
    this.metadata = metadata;
  }

  public String getChangeId() {
    return changeId;
  }

  public void setChangeId(String changeId) {
    this.changeId = changeId;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getChangeLogClass() {
    return changeLogClass;
  }

  public void setChangeLogClass(String changeLogClass) {
    this.changeLogClass = changeLogClass;
  }

  public String getChangeSetMethod() {
    return changeSetMethod;
  }

  public void setChangeSetMethod(String changeSetMethod) {
    this.changeSetMethod = changeSetMethod;
  }

  public String getMetadata() {
    return metadata;
  }

  public void setMetadata(String metadata) {
    this.metadata = metadata;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LegacyMigrationMappingFields that = (LegacyMigrationMappingFields) o;
    return Objects.equals(changeId, that.changeId) &&
        Objects.equals(author, that.author) &&
        Objects.equals(timestamp, that.timestamp) &&
        Objects.equals(changeLogClass, that.changeLogClass) &&
        Objects.equals(changeSetMethod, that.changeSetMethod) &&
        Objects.equals(metadata, that.metadata);
  }

  @Override
  public int hashCode() {
    return Objects.hash(changeId, author, timestamp, changeLogClass, changeSetMethod, metadata);
  }
}
