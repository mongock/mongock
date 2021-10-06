package io.mongock.runner.core.changelogs.test1;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;

@ChangeLog(order = "1")
public class ChangeLogSuccess11 {

  @ChangeSet(
      author = "testUser11",
      id = "ChangeSet_121",
      order = "1",
      runAlways = true,
      systemVersion = "1")
  public void method_111() {
  }


}
