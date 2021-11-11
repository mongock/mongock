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

/**
 * Entry in the changes collection log
 * Type: entity class.
 *
 * @since 27/07/2014
 */
public class ChangeEntry {

  @Field("executionId")
  private final String executionId;

  @Field("changeId")
  private final String changeId;

  @Field("author")
  private final String author;

  @Field("timestamp")
  private final Date timestamp;

  @Field("state")
  private final ChangeState state;

  @Field("type")
  private final ChangeType type;

  @Field("changeLogClass")
  private final String changeLogClass;

  @Field("changeSetMethod")
  private final String changeSetMethod;

  @Field("metadata")
  private final Object metadata;

  @Field("executionMillis")
  private final long executionMillis;

  @Field("executionHostname")
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
