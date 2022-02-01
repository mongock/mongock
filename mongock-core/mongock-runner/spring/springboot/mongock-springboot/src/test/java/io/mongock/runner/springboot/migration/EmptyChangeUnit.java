package io.mongock.runner.springboot.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

/**
 *
 * @since 30.07.14
 */
@ChangeUnit(id ="EmptyChangeUnit", order = "1")
public class EmptyChangeUnit {


  @Execution
  public void execution() {}

  @RollbackExecution
  public void rollback() {}

}
