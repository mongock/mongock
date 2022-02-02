package io.mongock.runner.standalone.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

/**
 * @since 30.07.14
 */
@ChangeUnit(id = "LongIOChangeUnit", order = "1")
public class LongIOChangeUnit {


  @Execution
  public void execution(ServiceStub service) throws InterruptedException {
    Thread.sleep(5000L);
    service.call();//this call is to force the lock ensuring process

  }

  @RollbackExecution
  public void rollback() {
  }

}
