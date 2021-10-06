package io.mongock.runner.core.changelogs.withDuplications.changesetsduplicated;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

@ChangeUnit(id="duplicated", order = "1", author = "mongock_test", systemVersion = "1")
public class ChangeLogDuplicated2 {


  @Execution
  public void changeSet() {

    //TODO NOTHING
  }

  @RollbackExecution
  public void rollback() {

  }


}
