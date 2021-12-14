package io.mongock.runner.core.changelogs.systemversion;


import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

@ChangeUnit(id = "change-unit-system-version-6", author = "mongock", order = "3", systemVersion = "6")
public class ChangeUnitSystemVersion6 {

  @Execution
  public void execution() {}

  @RollbackExecution
  public void rollbackExecution() {}

}
