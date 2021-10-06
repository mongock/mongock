package io.mongock.runner.core.changelogs.executor.test4_with_failfast;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import io.mongock.runner.core.util.DummyDependencyClass;

import java.util.concurrent.CountDownLatch;

@ChangeLog(order = "0")
public class ExecutorWithFailFastChangeLog {

  public final static CountDownLatch latch = new CountDownLatch(3);

  @ChangeSet(author = "executor", id = "newChangeSet", order = "1")
  public void newChangeSet(DummyDependencyClass dependency) {
    latch.countDown();
  }

  @ChangeSet(author = "executor", id = "runAlwaysAndNewChangeSet", order = "2", runAlways = true)
  public void runAlwaysAndNewChangeSet(DummyDependencyClass dependency) {
    latch.countDown();
  }

  @ChangeSet(author = "executor", id = "throwsException", order = "3")
  public void throwsException(DummyDependencyClass dependency) {
    latch.countDown();
    throw new RuntimeException("This method throws an exception");
  }

  @ChangeSet(author = "executor", id = "runAlwaysAndAlreadyExecutedChangeSet", order = "4", runAlways = true)
  public void runAlwaysAndAlreadyExecutedChangeSet(DummyDependencyClass dependency) {
    throw new RuntimeException("This method should not be executed");
  }


}
