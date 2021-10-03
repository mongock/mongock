package io.mongock.runner.core.changelogs.forchangeservice.changeloginterface;

import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;

@ChangeUnit(id="AdvanceChangeLogUniqueInPackage", order = "1", author = "mongock_test", systemVersion = "1")
public class AdvanceChangeLogUniqueInPackage {


  @Execution
  public void changeSet() {
  }

  @RollbackExecution
  public void rollback() {
  }


  @BeforeExecution
  public void before() {
  }

  @RollbackBeforeExecution
  public void rollbackBefore() {

  }

}
