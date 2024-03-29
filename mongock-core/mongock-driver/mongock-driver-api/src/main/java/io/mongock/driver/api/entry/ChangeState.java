package io.mongock.driver.api.entry;

public enum ChangeState {
  EXECUTED, FAILED, ROLLED_BACK, ROLLBACK_FAILED, IGNORED;

  public boolean isFailed() {
    return this == FAILED || this == ROLLED_BACK || this == ROLLBACK_FAILED;
  }
}
