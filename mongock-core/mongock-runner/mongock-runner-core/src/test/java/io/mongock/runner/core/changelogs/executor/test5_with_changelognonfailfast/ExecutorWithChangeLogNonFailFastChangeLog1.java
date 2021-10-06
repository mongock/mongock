package io.mongock.runner.core.changelogs.executor.test5_with_changelognonfailfast;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import io.mongock.runner.core.util.DummyDependencyClass;

import java.util.concurrent.CountDownLatch;

@ChangeLog(order = "1", failFast = false)
public class ExecutorWithChangeLogNonFailFastChangeLog1 {

  public final static CountDownLatch latch = new CountDownLatch(2);

  @ChangeSet(author = "executor", id = "newChangeSet11", order = "1")
  public void newChangeSet11(DummyDependencyClass dependency) {
    latch.countDown();
  }

  @ChangeSet(author = "executor", id = "newChangeSet12", order = "2")
  public void newChangeSet12(DummyDependencyClass dependency) {
    latch.countDown();
    throw new RuntimeException("This method throws an exception");
  }

  @ChangeSet(author = "executor", id = "newChangeSet13", order = "3")
  public void newChangeSet13(DummyDependencyClass dependency) {
    throw new RuntimeException("This method should not be executed");
  }
}
