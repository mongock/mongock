package com.github.cloudyrock.mongock;

import org.bson.Document;

import java.util.Date;

/**
 * Entry in the changes collection log
 * Type: entity class.
 *
 *
 * @since 27/07/2014
 */
class ChangeEntry {
  static final String EXECUTION_ID = "changeId";
  static final String KEY_CHANGEID = "changeId";
  static final String KEY_AUTHOR = "author";
  private static final String KEY_TIMESTAMP = "timestamp";
  private static final String KEY_CHANGELOGCLASS = "changeLogClass";
  private static final String KEY_CHANGESETMETHOD = "changeSetMethod";


  private final String executionId;
  private final String changeId;
  private final String author;
  private final Date timestamp;
  private final String changeLogClass;
  private final String changeSetMethodName;

  public ChangeEntry(String executionId, String changeId, String author, Date timestamp, String changeLogClass, String changeSetMethodName) {
    this.executionId = executionId;
    this.changeId = changeId;
    this.author = author;
    this.timestamp = new Date(timestamp.getTime());
    this.changeLogClass = changeLogClass;
    this.changeSetMethodName = changeSetMethodName;
  }

  Document buildFullDBObject() {
    return new Document()
        .append(EXECUTION_ID, this.executionId)
        .append(KEY_CHANGEID, this.changeId)
        .append(KEY_AUTHOR, this.author)
        .append(KEY_TIMESTAMP, this.timestamp)
        .append(KEY_CHANGELOGCLASS, this.changeLogClass)
        .append(KEY_CHANGESETMETHOD, this.changeSetMethodName);
  }

  public String getExecutionId() {
    return executionId;
  }

  String getChangeId() {
    return this.changeId;
  }

  String getAuthor() {
    return this.author;
  }

  Date getTimestamp() {
    return this.timestamp;
  }

  String getChangeLogClass() {
    return this.changeLogClass;
  }

  String getChangeSetMethodName() {
    return this.changeSetMethodName;
  }

  @Override
  public String toString() {

    return String.format(
        "Mongock change[%s] for method[%s.%s] in execution[%s] at %s by %s",
        changeId,
        changeLogClass,
        changeSetMethodName,
        executionId,
        timestamp,
        author);
  }
}
