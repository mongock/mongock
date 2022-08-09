package io.mongock.runner.core.changelogs.system.updatessystemtables;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.driver.api.common.SystemChange;

@SystemChange(updatesSystemTable = true)
@ChangeUnit(id="system-change-unit-updates-system-tables", order = "1", author = "mongock_test", systemVersion = "1")
public class SystemChangeUnitUpdatesSystemTables {

  public static boolean isExecuted = false;
  
  @Execution
  public void changeSet() {
    isExecuted = true;
  }

  @RollbackExecution
  public void rollback() {
  }

}
