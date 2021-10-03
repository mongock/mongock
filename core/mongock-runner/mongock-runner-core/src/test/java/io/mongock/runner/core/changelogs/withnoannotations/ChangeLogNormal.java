package io.mongock.runner.core.changelogs.withnoannotations;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;

@ChangeLog(order = "1")
public class ChangeLogNormal {

  @ChangeSet(
      author = "testUser11",
      id = "changeSet_0",
      order = "1",
      runAlways = true,
      systemVersion = "1")
  public void method_1() {
  }
}
