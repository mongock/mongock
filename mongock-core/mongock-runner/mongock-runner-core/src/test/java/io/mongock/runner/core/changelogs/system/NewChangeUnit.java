package io.mongock.runner.core.changelogs.system;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.driver.api.common.SystemChange;

@ChangeUnit(id="new-change-unit", order = "1", author = "mongock_test", systemVersion = "1")
public class NewChangeUnit {

  public static boolean isExecuted = false;

  @Execution
  public void changeSet() {
    isExecuted = true;
  }

  @RollbackExecution
  public void rollback() {
  }

}
