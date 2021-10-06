package io.mongock.runner.core.event.result;

public class MigrationFailedResult extends MigrationResult {

  private final Exception exception;

  public MigrationFailedResult(Exception exception) {
    super(false);
    this.exception = exception;
  }

  public Exception getException() {
    return exception;
  }
}
