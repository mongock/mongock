package io.mongock.runner.core.changelogs.skipmigration.alreadyexecuted;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import io.mongock.runner.core.util.DummyDependencyClass;

@ChangeLog
public class ChangeLogAlreadyExecuted {

  @ChangeSet(author = "executor", id = "alreadyExecuted", order = "1")
  public void alreadyExecuted(DummyDependencyClass dependency) {
    throw new RuntimeException("This method should not be executed, as it's supposed to be already executed");
  }

  @ChangeSet(author = "executor", id = "alreadyExecuted2", order = "2")
  public void alreadyExecuted2(DummyDependencyClass dependency) {
    throw new RuntimeException("This method should not be executed, as it's supposed to be already executed");
  }
}
