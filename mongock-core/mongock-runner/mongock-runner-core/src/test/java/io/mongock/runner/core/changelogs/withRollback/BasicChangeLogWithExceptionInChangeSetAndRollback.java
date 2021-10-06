package io.mongock.runner.core.changelogs.withRollback;

import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.api.exception.MongockException;

import java.util.concurrent.CountDownLatch;

@ChangeUnit(id="changeset_with_exception_and_rollback_1", order = "1", author = "mongock_test", systemVersion = "1")
public class BasicChangeLogWithExceptionInChangeSetAndRollback {

  public final static CountDownLatch rollbackCalledLatch = new CountDownLatch(1);


  @Execution
  public void changeSet() {
    if(true) throw new MongockException();
  }

  @RollbackExecution
  public void rollback() {
    rollbackCalledLatch.countDown();
  }


}
