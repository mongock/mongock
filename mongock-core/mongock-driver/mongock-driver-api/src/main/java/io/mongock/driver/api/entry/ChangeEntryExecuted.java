package io.mongock.driver.api.entry;

import java.util.Date;

public class ChangeEntryExecuted {

  private final String changeId;

  private final String author;

  private final Date timestamp;
  
  private final String changeLogClass;

  private final String changeSetMethod;

  public ChangeEntryExecuted(
                     String changeId,
                     String author,
                     Date timestamp,
                     String changeLogClass,
                     String changeSetMethod) {
    this.changeId = changeId;
    this.author = author;
    this.timestamp = new Date(timestamp.getTime());
    this.changeLogClass = changeLogClass;
    this.changeSetMethod = changeSetMethod;
  }
  public ChangeEntryExecuted(ChangeEntry entry) {
    this(entry.getChangeId(), entry.getAuthor(), entry.getTimestamp(), entry.getChangeLogClass(), entry.getChangeSetMethod());
  }

  public String getChangeId() {
    return this.changeId;
  }

  public String getAuthor() {
    return this.author;
  }

  public Date getTimestamp() {
    return this.timestamp;
  }
  
  public String getChangeLogClass() {
    return this.changeLogClass;
  }

  public String getChangeSetMethod() {
    return this.changeSetMethod;
  }

  @Override
  public String toString() {
    return "ChangeEntryExecuted{" +
        "changeId='" + changeId + '\'' +
        ", author='" + author + '\'' +
        ", timestamp=" + timestamp +
        ", changeLogClass='" + changeLogClass + '\'' +
        ", changeSetMethod='" + changeSetMethod + '\'' +
        '}';
  }
}
