package io.mongock.runner.core.changelogs.systemversion;


import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

@ChangeUnit(id = "change-unit-system-version-3", author = "mongock", order = "2", systemVersion = "3")
public class ChangeUnitSystemVersion3 {

  @Execution
  public void execution() {}

  @RollbackExecution
  public void rollbackExecution() {}

}
