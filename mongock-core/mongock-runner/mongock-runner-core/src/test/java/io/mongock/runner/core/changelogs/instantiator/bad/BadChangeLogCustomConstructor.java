package io.mongock.runner.core.changelogs.instantiator.bad;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;

@ChangeLog
public class BadChangeLogCustomConstructor {
  public BadChangeLogCustomConstructor() {
  }

  @ChangeSet(order = "001", id = "test", author = "test")
  public void migration() {
  }
}
