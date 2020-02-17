package com.github.cloudyrock.mongock.test.changelogs.runAlways;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;


@ChangeLog(order = "1")
public class RunAlwaysChangeLog {

  @ChangeSet(author = "testuser", id = "runAlways", order = "01", runAlways = true)
  public void testChangeSet() {

    System.out.println("invoked 1");

  }

  @ChangeSet(author = "testuser", id = "noRunAlways", order = "02")
  public void testChangeSet2() {

    System.out.println("invoked 2");

  }

}
