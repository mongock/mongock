package io.mongock.runner.core.changelogs.with_author_empty;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;

@ChangeLog(order = "1")
public class ChangeLogWithAuthorEmpty {

  @ChangeSet(
      author = "",
      id = "changeSet_0",
      order = "1",
      runAlways = true,
      systemVersion = "1")
  public void method_1() {
  }
}
