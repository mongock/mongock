package io.mongock.runner.core.changelogs.systemversion;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;

@ChangeLog(order = "1")
public class ChangeLogSystemVersion {

  @ChangeSet(
      author = "testUser1",
      id = "ChangeSet_1",
      order = "1",
      runAlways = true,
      systemVersion = "1")
  public void method_1() {
  }

  @ChangeSet(
      author = "testUser1",
      id = "ChangeSet_2",
      order = "2",
      runAlways = true,
      systemVersion = "2")
  public void method_2() {
  }

  @ChangeSet(
      author = "testUser1",
      id = "ChangeSet_3.0",
      order = "3",
      runAlways = true,
      systemVersion = "3.0")
  public void method_3Dot0() {
  }

  @ChangeSet(
      author = "testUser1",
      id = "ChangeSet_4",
      order = "4",
      runAlways = true,
      systemVersion = "4")
  public void method_4() {
  }

  @ChangeSet(
      author = "testUser1",
      id = "ChangeSet_5",
      order = "5",
      runAlways = true,
      systemVersion = "5")
  public void method_5() {
  }

  @ChangeSet(
      author = "testUser1",
      id = "ChangeSet_6",
      order = "6",
      runAlways = true,
      systemVersion = "6")
  public void method_6() {
  }

  @ChangeSet(
      author = "testUser1",
      id = "ChangeSet_2018",
      order = "6",
      runAlways = true,
      systemVersion = "2018")
  public void method_2018() {
  }

  @ChangeSet(
      author = "testUser1",
      id = "ChangeSet_2019",
      order = "6",
      runAlways = true,
      systemVersion = "2019")
  public void method_2019() {
  }


}
