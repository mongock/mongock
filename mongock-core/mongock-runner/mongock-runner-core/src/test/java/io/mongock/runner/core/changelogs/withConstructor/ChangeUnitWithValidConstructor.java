package io.mongock.runner.core.changelogs.withConstructor;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

@ChangeUnit(id = "ChangeUnitWithValidConstructor", order = "1")
public class ChangeUnitWithValidConstructor {

  public ChangeUnitWithValidConstructor() {
  }

  @Execution
  public void execution() {
  }

  @RollbackExecution
  public void rollbackExecution() {
  }
}
