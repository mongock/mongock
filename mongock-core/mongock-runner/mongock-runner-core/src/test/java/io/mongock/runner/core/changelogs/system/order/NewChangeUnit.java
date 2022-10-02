package io.mongock.runner.core.changelogs.system.order;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

@ChangeUnit(id="new-change-unit", order = "1", author = "mongock_test", systemVersion = "1")
public class NewChangeUnit {

  public static boolean isExecuted = false;

  @Execution
  public void changeSet() {
    isExecuted = true;
  }

  @RollbackExecution
  public void rollback() {
    // Do nothing
  }

}
