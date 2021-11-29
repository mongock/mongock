package io.mongock.runner.core.changelogs.with_author_empty;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

@ChangeUnit(id = "changeLog_with_nno_author", order = "1")
public class ChangeUnitWithAuthorEmpty {

  @Execution
  public void execution() { }

  @RollbackExecution
  public void rollback() {}
}
