package io.mongock.runner.core.changelogs.withRollback;

import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.api.exception.MongockException;

@ChangeUnit(id="changeset_with_exception_in_rollback_1", order = "1", author = "mongock_test", systemVersion = "1")
public class BasicChangeLogWithExceptionInRollback {

  @Execution
  public void changeSet() {
    if(true) throw new MongockException();
  }

  @RollbackExecution
  public void rollback() {
    if(true) throw new MongockException();
  }


}
