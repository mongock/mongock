package io.mongock.driver.remote.repository.external;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.mongock.driver.api.entry.ChangeState;
import io.mongock.driver.api.entry.ChangeType;
import io.mongock.utils.StringUtils;

import java.time.LocalDateTime;


public class ChangeEntryDto {

  private final String executionId;
  private final String changeId;
  private final String author;
  private final LocalDateTime timestamp;
  private final ChangeState state;
  private final ChangeType type;
  private final String changeLogClass;
  private final String changeSetMethod;
  private final Object metadata;
  private final long executionMillis;
  private final String executionHostname;
//  private final String errorTrace;


  public ChangeEntryDto(String executionId,
                        String changeId,
                        String author,
                        LocalDateTime timestamp,
                        ChangeState state,
                        ChangeType type,
                        String changeLogClass,
                        String changeSetMethod,
                        long executionMillis,
                        String executionHostname,
                        Object metadata
//      , String errorTrace
  ) {
    this.executionId = executionId;
    this.changeId = changeId;
    this.author = author;
    this.timestamp = timestamp;
    this.state = state != null ? state : ChangeState.EXECUTED;
    this.type = type != null ? type : ChangeType.EXECUTION;
    this.changeLogClass = changeLogClass;
    this.changeSetMethod = changeSetMethod;
    this.executionMillis = executionMillis;
    this.executionHostname = executionHostname;
    this.metadata = metadata;
//    this.errorTrace = errorTrace;
  }


  public String getExecutionId() {
    return executionId;
  }

  public String getChangeId() {
    return this.changeId;
  }

  public String getId() {
    return getChangeId();
  }

  public String getAuthor() {
    return this.author;
  }

  public String getChangeLogClassString() {
    return StringUtils.getSimpleClassName(changeLogClass);
  }

  public String getMethodNameString() {
    return getChangeSetMethod();
  }

  //TODO change this temporal fix to facilitate tests
  public String getTimestamp() {
    return this.timestamp.toString();
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

//  public Optional<String> getErrorTrace() {
//    return Optional.ofNullable(errorTrace);
//  }

//  @Override
//  public boolean equals(Object o) {
//    if (this == o) return true;
//    if (!(o instanceof ChangeEntryDto)) return false;
//
//    ChangeEntryDto that = (ChangeEntryDto) o;
//
//    if (getExecutionMillis() != that.getExecutionMillis()) return false;
//    if (!getExecutionId().equals(that.getExecutionId())) return false;
//    if (!getChangeId().equals(that.getChangeId())) return false;
//    if (!getAuthor().equals(that.getAuthor())) return false;
//    if (!getTimestamp().equals(that.getTimestamp())) return false;
//    if (getState() != that.getState()) return false;
//    if (getType() != that.getType()) return false;
//    if (!getChangeLogClass().equals(that.getChangeLogClass())) return false;
//    if (!getChangeSetMethod().equals(that.getChangeSetMethod())) return false;
//    if (getMetadata() != null ? !getMetadata().equals(that.getMetadata()) : that.getMetadata() != null) return false;
//    if (getExecutionHostname() != null ? !getExecutionHostname().equals(that.getExecutionHostname()) : that.getExecutionHostname() != null)
//      return false;
//    return getErrorTrace() != null ? getErrorTrace().equals(that.getErrorTrace()) : that.getErrorTrace() == null;
//  }
//
//  @Override
//  public int hashCode() {
//    int result = getExecutionId().hashCode();
//    result = 31 * result + getChangeId().hashCode();
//    result = 31 * result + getAuthor().hashCode();
//    result = 31 * result + getTimestamp().hashCode();
//    result = 31 * result + getState().hashCode();
//    result = 31 * result + getType().hashCode();
//    result = 31 * result + getChangeLogClass().hashCode();
//    result = 31 * result + getChangeSetMethod().hashCode();
//    result = 31 * result + (getMetadata() != null ? getMetadata().hashCode() : 0);
//    result = 31 * result + (int) (getExecutionMillis() ^ (getExecutionMillis() >>> 32));
//    result = 31 * result + (getExecutionHostname() != null ? getExecutionHostname().hashCode() : 0);
//    result = 31 * result + (getErrorTrace() != null ? getErrorTrace().hashCode() : 0);
//    return result;
//  }
}
