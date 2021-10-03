package io.mongock.runner.core.event.result;

public class MigrationResult {

  private final boolean success;

  protected MigrationResult(boolean success) {
    this.success = success;
  }

  public boolean isSuccess() {
    return success;
  }
}
