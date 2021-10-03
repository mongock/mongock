package io.mongock.runner.core.event.result;

public class MigrationSuccessResult extends MigrationResult {

  private final Object result;

  public MigrationSuccessResult(Object result) {
    super(true);
    this.result = result;
  }

  public Object getResult() {
    return result;
  }
}
