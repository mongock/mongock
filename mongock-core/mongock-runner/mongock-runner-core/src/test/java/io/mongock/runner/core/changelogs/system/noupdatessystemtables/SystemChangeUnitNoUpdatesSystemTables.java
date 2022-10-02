package io.mongock.runner.core.changelogs.system.noupdatessystemtables;



import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.driver.api.common.SystemChange;

@SystemChange(updatesSystemTable = false)
@ChangeUnit(id="system-change-unit-no-updates-system-tables", order = "1", author = "mongock_test", systemVersion = "1")
public class SystemChangeUnitNoUpdatesSystemTables {

  public static boolean isExecuted = false;
  
  @Execution
  public void changeSet() {
    isExecuted = true;
  }

  @RollbackExecution
  public void rollback() {
  }

}
