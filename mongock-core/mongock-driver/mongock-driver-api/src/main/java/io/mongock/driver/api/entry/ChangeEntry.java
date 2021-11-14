package io.mongock.driver.api.entry;

import io.mongock.utils.StringUtils;
import io.mongock.utils.field.Field;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static io.mongock.driver.api.entry.ChangeState.EXECUTED;
import static io.mongock.driver.api.entry.ChangeState.FAILED;
import static io.mongock.driver.api.entry.ChangeState.ROLLBACK_FAILED;
import static io.mongock.driver.api.entry.ChangeState.ROLLED_BACK;
import static io.mongock.utils.field.Field.KeyType.PRIMARY;

/**
 * Entry in the changes collection log
 * Type: entity class.
 *
 * @since 27/07/2014
 */
public class ChangeEntry {

  public static final String KEY_EXECUTION_ID = "executionId";
  public static final String KEY_CHANGE_ID = "changeId";
  public static final String KEY_AUTHOR = "author";
  public static final String KEY_TIMESTAMP = "timestamp";
  public static final String KEY_STATE = "state";
  public static final String KEY_TYPE = "type";
  public static final String KEY_CHANGELOG_CLASS = "changeLogClass";
  public static final String KEY_CHANGESET_METHOD = "changeSetMethod";
  public static final String KEY_METADATA = "metadata";
  public static final String KEY_EXECUTION_MILLIS = "executionMillis";
  public static final String KEY_EXECUTION_HOST_NAMEA = "executionHostname";



  @Field(value = KEY_EXECUTION_ID, type = PRIMARY)
  private final String executionId;

  @Field(value = KEY_CHANGE_ID, type = PRIMARY)
  private final String changeId;

  @Field(value = KEY_AUTHOR, type = PRIMARY)
  private final String author;

  @Field(KEY_TIMESTAMP)
  private final Date timestamp;

  @Field(KEY_STATE)
  private final ChangeState state;

  @Field(KEY_TYPE)
  private final ChangeType type;

  @Field(KEY_CHANGELOG_CLASS)
  private final String changeLogClass;

  @Field(KEY_CHANGESET_METHOD)
  private final String changeSetMethod;

  @Field(KEY_METADATA)
  private final Object metadata;

  @Field(KEY_EXECUTION_MILLIS)
  private final long executionMillis;

  @Field(KEY_EXECUTION_HOST_NAMEA)
  private final String executionHostname;

  public ChangeEntry(String executionId,
                     String changeId,
                     String author,
                     Date timestamp,
                     ChangeState state,
                     ChangeType type,
                     String changeLogClass,
                     String changeSetMethod,
                     long executionMillis,
                     String executionHostname,
                     Object metadata) {
    this.executionId = executionId;
    this.changeId = changeId;
    this.author = author;
    this.timestamp = new Date(timestamp.getTime());
    this.state = state != null ? state : ChangeState.EXECUTED;
    this.type = type != null ? type : ChangeType.EXECUTION;
    this.changeLogClass = changeLogClass;
    this.changeSetMethod = changeSetMethod;
    this.executionMillis = executionMillis;
    this.executionHostname = executionHostname;
    this.metadata = metadata;
  }

  public static ChangeEntry createInstance(String executionId,
                                           String author,
                                           ChangeState state,
                                           ChangeType type,
                                           String changeSetId,
                                           String changeSetClassName,
                                           String changeSetName,
                                           long executionMillis,
                                           String executionHostname,
                                           Object metadata) {
    return new ChangeEntry(
        executionId,
        changeSetId,
        author,
        new Date(),
        state,
        type,
        changeSetClassName,
        changeSetName,
        executionMillis,
        executionHostname,
        metadata);
  }



  public String getExecutionId() {
    return executionId;
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

  public ChangeState getState() {
    return state;
  }

  public String getChangeLogClass() {
    return this.changeLogClass;
  }

  public String getChangeSetMethod() {
    return this.changeSetMethod;
  }

  public long getExecutionMillis() {
    return executionMillis;
  }

  public String getExecutionHostname() {
    return executionHostname;
  }

  public Object getMetadata() {
    return metadata;
  }

  public ChangeType getType() {
    return type;
  }

  @Override
  public String toString() {
    String sb = "ChangeEntry{" + "executionId='" + executionId + '\'' +
        ", changeId='" + changeId + '\'' +
        ", author='" + author + '\'' +
        ", timestamp=" + timestamp +
        ", state=" + state +
        ", type=" + type +
        ", changeLogClass='" + changeLogClass + '\'' +
        ", changeSetMethod='" + changeSetMethod + '\'' +
        ", metadata=" + metadata +
        ", executionMillis=" + executionMillis +
        ", executionHostname='" + executionHostname + '\'' +
        '}';
    return sb;
  }

  public String toPrettyString() {
    return "ChangeEntry{" +
        "\"id\"=\"" + changeId + "\"" +
        ", \"author\"=\"" + author + "\"" +
        ", \"class\"=\"" + StringUtils.getSimpleClassName(changeLogClass) + "\"" +
        ", \"method\"=\"" + changeSetMethod + "\"" +
        '}';
  }

  private static final Set<ChangeState> RELEVANT_STATES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(EXECUTED, ROLLED_BACK, FAILED, ROLLBACK_FAILED)));

  public boolean hasRelevantState() {
    return state == null || RELEVANT_STATES.contains(state);
  }


  public boolean isExecuted() {
    return state == null || EXECUTED == state ;
  }
}
