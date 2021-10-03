package io.mongock.runner.core.event;

import io.mongock.runner.core.event.result.MigrationResult;
import io.mongock.runner.core.event.result.MigrationSuccessResult;

public class MigrationSuccessEvent implements MongockResultEvent {

  private final MigrationSuccessResult migrationResult;

  public MigrationSuccessEvent(MigrationSuccessResult migrationResult) {
    this.migrationResult = migrationResult;
  }


  @Override
  public MigrationResult getMigrationResult() {
    return migrationResult;
  }
}
