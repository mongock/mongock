package io.mongock.runner.core.changelogs.forchangeservice.basichangeloginterface;

import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.RollbackExecution;

@ChangeUnit(id="BasicChangeLogUniqueInPackage", order = "2", author = "mongock_test", systemVersion = "1")
public class BasicChangeLogUniqueInPackage {

  @Execution
  public void changeSet() {
  }

  @RollbackExecution
  public void rollback() {
  }

}
