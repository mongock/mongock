package com.github.cloudyrock.standalone.test.changelogs.allsuccess.insideallsuccess;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;


@ChangeLog(order = "1")
public class InsideAllSuccess1 {


  @ChangeSet(author = "testuser", id = "test1", order = "01")
  public void method1() {
  }


}
