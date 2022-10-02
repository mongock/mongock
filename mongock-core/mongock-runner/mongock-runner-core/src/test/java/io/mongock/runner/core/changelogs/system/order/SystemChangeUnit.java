package io.mongock.runner.core.changelogs.system.order;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.driver.api.common.SystemChange;

@SystemChange(updatesSystemTable = true)
@ChangeUnit(id="system-change-unit", order = "2", author = "mongock_test", systemVersion = "1")
public class SystemChangeUnit {

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
