package io.mongock.runner.core.changelogs.withnoannotations;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;

@ChangeLog(order = "2")
public class ChangeLogWithBoth {

  @ChangeSet(
      author = "testUser11",
      id = "changeSet_1",
      order = "2",
      runAlways = true,
      systemVersion = "1")
  public void method_1() {
  }

  public void method_2() {
  }

}
