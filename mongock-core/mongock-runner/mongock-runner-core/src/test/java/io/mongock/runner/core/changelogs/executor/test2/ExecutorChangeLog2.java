package io.mongock.runner.core.changelogs.executor.test2;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import io.mongock.runner.core.util.DummyDependencyClass;

@ChangeLog(order = "0")
public class ExecutorChangeLog2 {


  @ChangeSet(author = "executor", id = "newChangeSet", order = "1")
  public void newChangeSet(DummyDependencyClass dependency) {
  }


}
