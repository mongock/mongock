package io.mongock.runner.core.changelogs.executor.test6_with_changelogfailfast;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import io.mongock.runner.core.util.DummyDependencyClass;

@ChangeLog(order = "2")
public class ExecutorWithChangeLogFailFastChangeLog2 {

  @ChangeSet(author = "executor", id = "newChangeSet21", order = "1")
  public void newChangeSet21(DummyDependencyClass dependency) {
    throw new RuntimeException("This method should not be executed");
  }

  @ChangeSet(author = "executor", id = "newChangeSet22", order = "2")
  public void newChangeSet22(DummyDependencyClass dependency) {
    throw new RuntimeException("This method should not be executed");
  }
}
