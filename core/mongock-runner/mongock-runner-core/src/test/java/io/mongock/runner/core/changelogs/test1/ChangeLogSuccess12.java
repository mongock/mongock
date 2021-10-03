package io.mongock.runner.core.changelogs.test1;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;

@ChangeLog(order = "2")
public class ChangeLogSuccess12 {

  @ChangeSet(
      author = "testUser12",
      id = "ChangeSet_122",
      order = "2",
      runAlways = true,
      systemVersion = "2")
  public void method_121() {
    System.out.println(ChangeLogSuccess12.class.getCanonicalName() + ".method_121()");
  }


}
