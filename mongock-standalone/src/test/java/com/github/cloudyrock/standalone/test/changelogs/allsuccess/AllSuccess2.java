package com.github.cloudyrock.standalone.test.changelogs.allsuccess;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;


@ChangeLog(order = "1")
public class AllSuccess1 {


  @ChangeSet(author = "testuser", id = "test1", order = "01")
  public void method1() {
  }


}
