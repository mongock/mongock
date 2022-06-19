package io.mongock.runner.core.changelogs.withConstructor;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.ChangeUnitConstructor;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

@ChangeUnit(id = "ChangeUnitWithValidConstructorsHavingChangeUnitConstructor", order = "1")
public class ChangeUnitWithValidConstructorsHavingChangeUnitConstructor {

  public static final String DUMMY_VALUE = "dummyValue";
  private final String dummy;

  public ChangeUnitWithValidConstructorsHavingChangeUnitConstructor(String dummy) {
    this.dummy = dummy;
  }


  @ChangeUnitConstructor
  public ChangeUnitWithValidConstructorsHavingChangeUnitConstructor() {
    this.dummy = DUMMY_VALUE;
  }

  @Execution
  public void execution() {
  }

  @RollbackExecution
  public void rollbackExecution() {
  }

  public String getDummy() {
    return dummy;
  }
}
