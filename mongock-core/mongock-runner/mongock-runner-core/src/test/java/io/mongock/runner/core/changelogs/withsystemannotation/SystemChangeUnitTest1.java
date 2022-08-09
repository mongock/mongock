package io.mongock.runner.core.changelogs.withsystemannotation;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.driver.api.common.SystemChange;

@SystemChange
@ChangeUnit(id = "system-changeunit-test-1", author = "mongock-test", order = "1")
public class SystemChangeUnitTest1 {
  
  @Execution
  public void execution() {
    // Testing purpose
  }
  
  @RollbackExecution
  public void rollbackExecution() {
    // Testing purpose
  }
}
