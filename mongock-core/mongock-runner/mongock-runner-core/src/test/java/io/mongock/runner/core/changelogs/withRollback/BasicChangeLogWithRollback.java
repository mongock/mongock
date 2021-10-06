package io.mongock.runner.core.changelogs.withRollback;

import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.RollbackExecution;

import java.util.concurrent.CountDownLatch;

@ChangeUnit(id="changeset_with_rollback_1", order = "1", author = "mongock_test", systemVersion = "1")
public class BasicChangeLogWithRollback {

  public final static CountDownLatch rollbackCalledLatch = new CountDownLatch(2);

  @Execution
  public void changeSet() {

    //TODO NOTHING
  }

  @RollbackExecution
  public void rollback() {
    rollbackCalledLatch.countDown();
  }


}
