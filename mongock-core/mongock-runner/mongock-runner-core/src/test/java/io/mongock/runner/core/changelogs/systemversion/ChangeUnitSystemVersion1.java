package io.mongock.runner.core.changelogs.systemversion;


import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

@ChangeUnit(id = "change-unit-system-version-1", author = "mongock", order = "2", systemVersion = "1")
public class ChangeUnitSystemVersion1 {

  @Execution
  public void execution() {}

  @RollbackExecution
  public void rollbackExecution() {}

}
