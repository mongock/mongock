package io.mongock.runner.core.executor.system.changes;

import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.driver.api.common.SystemChange;
import io.mongock.driver.api.entry.ChangeEntryService;

/**
 * This ChangeUnit ensures the systemChange field is present in the table changeLogItems and set the default values:
 * "true" to all systemChangeUnits and "false" to all those are user normal
 * 
 * TODO: Execution method should set "true" to all Legacy ChangeUnits and existing System ChangeUnits
**/

@SystemChange
@ChangeUnit(id = "system-change-10001", author = "mongock", order = "10001")
public class SystemChangeUnit10001 {
  
  @BeforeExecution
  public void beforeExecution(ChangeEntryService changeEntryService) {
    // Ensure that mongockChangeLog collection/table structure is OK
    changeEntryService.ensureAllFields();
  }
  
  @RollbackBeforeExecution
  public void rollbackBeforeExecution() {
    // Do nothing
  }
  
  @Execution
  public void execution(ChangeEntryService changeEntryService) {
    // Update "systemUpdate" field to false (default)
    changeEntryService.getEntriesLog()
                      .stream()
                      .filter(f -> f.getChangeLogClass() == null || !f.getChangeLogClass().equals(this.getClass().getName()))
                      .peek(c -> c.setSystemChange(false))
                      .forEach(changeEntryService::saveOrUpdate);
  }
  
  @RollbackExecution
  public void rollbackExecution() {
    // Do nothing
  }
  
}
