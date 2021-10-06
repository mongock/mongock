package io.mongock.runner.core.changelogs.withRollback;

import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;

import java.util.concurrent.CountDownLatch;

@ChangeUnit(id="AdvanceChangeLogWithBeforeFailing", order = "1", author = "mongock_test", systemVersion = "1")
public class AdvanceChangeLogWithBeforeFailing {

  public final static CountDownLatch rollbackCalledLatch = new CountDownLatch(2);

  @Execution
  public void changeSet() {
  }

  @RollbackExecution
  public void rollback() {
    rollbackCalledLatch.countDown();
  }


  @BeforeExecution
  public void before() {

    if(true) throw new RuntimeException("Expected exception in " + AdvanceChangeLogWithBeforeFailing.class + " changeLog[ChangeSet]");
  }

  @RollbackBeforeExecution
  public void rollbackBefore() {
    rollbackCalledLatch.countDown();
  }

}
