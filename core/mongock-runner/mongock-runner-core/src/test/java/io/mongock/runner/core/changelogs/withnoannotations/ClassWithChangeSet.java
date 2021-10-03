package io.mongock.runner.core.changelogs.withnoannotations;

import com.github.cloudyrock.mongock.ChangeSet;


public class ClassWithChangeSet {

  @ChangeSet(
      author = "testUser11",
      id = "changeSet_2",
      order = "1",
      runAlways = true,
      systemVersion = "1")
  public void method_1() {
  }

  public void method() {
  }

}
