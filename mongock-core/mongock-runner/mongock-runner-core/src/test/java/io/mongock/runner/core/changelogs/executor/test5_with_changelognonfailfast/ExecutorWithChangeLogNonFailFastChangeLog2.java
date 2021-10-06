package io.mongock.runner.core.changelogs.executor.test5_with_changelognonfailfast;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import io.mongock.runner.core.util.DummyDependencyClass;

import java.util.concurrent.CountDownLatch;

@ChangeLog(order = "2")
public class ExecutorWithChangeLogNonFailFastChangeLog2 {

  public final static CountDownLatch latch = new CountDownLatch(2);

  @ChangeSet(author = "executor", id = "newChangeSet21", order = "1")
  public void newChangeSet21(DummyDependencyClass dependency) {
    latch.countDown();
  }

  @ChangeSet(author = "executor", id = "newChangeSet22", order = "2")
  public void newChangeSet22(DummyDependencyClass dependency) {
    latch.countDown();
  }
}
