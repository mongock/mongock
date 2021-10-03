package io.mongock.runner.core.event;

import io.mongock.runner.core.event.result.MigrationFailedResult;

public class MigrationFailureEvent implements MongockResultEvent {

  private final MigrationFailedResult migrationResult;

  public MigrationFailureEvent(Exception exception) {

    this.migrationResult = new MigrationFailedResult(exception);
  }

  public Exception getException() {
    return migrationResult.getException();
  }

  @Override
  public MigrationFailedResult getMigrationResult() {
    return migrationResult;
  }
}
