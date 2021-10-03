package io.mongock.runner.core.changelogs.withRollback;

import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;

import java.util.concurrent.CountDownLatch;

@ChangeUnit(id="AdvanceChangeLogWithBeforeAndChangeSetFailing", order = "2", author = "mongock_test", systemVersion = "1")
public class AdvanceChangeLogWithBeforeAndChangeSetFailing {

  public static boolean rollbackCalled = false;
  public static boolean rollbackBeforeCalled = false;
  public final static CountDownLatch rollbackCalledLatch = new CountDownLatch(2);


  @Execution
  public void changeSet() {
    rollbackCalled = false;
    rollbackBeforeCalled = false;
    if(true) throw new RuntimeException("Expected exception in " + AdvanceChangeLogWithBeforeAndChangeSetFailing.class + " changeLog[ChangeSet]");
  }

  @RollbackExecution
  public void rollback() {
    rollbackCalled = true;
    rollbackCalledLatch.countDown();
  }


  @BeforeExecution
  public void before() {
  }

  @RollbackBeforeExecution
  public void rollbackBefore() {
    rollbackBeforeCalled = true;
    rollbackCalledLatch.countDown();
  }

}
