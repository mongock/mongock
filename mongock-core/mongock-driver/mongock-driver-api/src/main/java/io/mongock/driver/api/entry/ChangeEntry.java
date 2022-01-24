package io.mongock.driver.api.entry;

import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.util.ChangePrintable;
import io.mongock.utils.StringUtils;
import io.mongock.utils.field.Field;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
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
public class ChangeEntry implements ChangePrintable {

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
  public static final String KEY_EXECUTION_HOST_NAME = "executionHostname";
  public static final String KEY_ERROR_TRACE = "errorTrace";


  @Field(value = KEY_EXECUTION_ID, type = PRIMARY)
  protected String executionId;

  @Field(value = KEY_CHANGE_ID, type = PRIMARY)
  protected String changeId;

  @Field(value = KEY_AUTHOR, type = PRIMARY)
  protected String author;

  @Field(KEY_TIMESTAMP)
  protected Date timestamp;

  @Field(KEY_STATE)
  protected ChangeState state;

  @Field(KEY_TYPE)
  protected ChangeType type;

  @Field(KEY_CHANGELOG_CLASS)
  protected String changeLogClass;

  @Field(KEY_CHANGESET_METHOD)
  protected String changeSetMethod;

  @Field(KEY_METADATA)
  protected Object metadata;

  @Field(KEY_EXECUTION_MILLIS)
  protected long executionMillis;

  @Field(KEY_EXECUTION_HOST_NAME)
  protected String executionHostname;

  @Field(KEY_ERROR_TRACE)
  protected String errorTrace;
  
  protected Date originalTimestamp;

  public ChangeEntry() {}

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
    this(executionId, changeId, author, timestamp, state, type, changeLogClass, changeSetMethod, executionMillis, executionHostname, metadata, null);
  }

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
                     Object metadata,
                     String errorTrace) {
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
    this.errorTrace = errorTrace;
    this.originalTimestamp = null;//TODO: To be assigned when we could save value en DB
  }


  public static ChangeEntry instance(String executionId,
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

  public static ChangeEntry failedInstance(String executionId,
                                           String author,
                                           ChangeState state,
                                           ChangeType type,
                                           String changeSetId,
                                           String changeSetClassName,
                                           String changeSetName,
                                           long executionMillis,
                                           String executionHostname,
                                           Object metadata,
                                           String error) {
    if(!state.isFailed()) {
      throw new MongockException("Creating a failed instance of changeEntry with a non-failed stated: " + state.name());
    }
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
        metadata,
        error);
  }


  public String getExecutionId() {
    return executionId;
  }

  public String getChangeId() {
    return this.changeId;
  }

  @Override
  public String getId() {
    return getChangeId();
  }

  @Override
  public String getAuthor() {
    return this.author;
  }

  @Override
  public String getChangeLogClassString() {
    return StringUtils.getSimpleClassName(changeLogClass);
  }

  @Override
  public String getMethodNameString() {
    return getChangeSetMethod();
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

  public Optional<String> getErrorTrace() {
    return Optional.ofNullable(errorTrace);
  }
  
  public Date getOriginalTimestamp() {
    return this.originalTimestamp;
  }
  
  public void setOriginalTimestamp(Date originalTimestamp) {
    this.originalTimestamp = originalTimestamp;
  }

  @Override
  public String toString() {
    return "ChangeEntry{" + "executionId='" + executionId + '\'' +
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
  }

  private static final Set<ChangeState> RELEVANT_STATES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(EXECUTED, ROLLED_BACK, FAILED, ROLLBACK_FAILED)));

  public boolean hasRelevantState() {
    return state == null || RELEVANT_STATES.contains(state);
  }


  public boolean isExecuted() {
    return state == null || EXECUTED == state;
  }
}
