package io.mongock.runner.core.changelogs.withConstructor;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.ChangeUnitConstructor;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

@ChangeUnit(id = "ChangeUnitWithMoreThanOneChangeUnitConstructor", order = "1")
public class ChangeUnitWithMoreThanOneChangeUnitConstructor {

  @ChangeUnitConstructor
  public ChangeUnitWithMoreThanOneChangeUnitConstructor() {
  }

  @ChangeUnitConstructor
  public ChangeUnitWithMoreThanOneChangeUnitConstructor(String dummy) {
  }

  @Execution
  public void execution() {
  }

  @RollbackExecution
  public void rollbackExecution() {
  }
}
