package com.github.cloudyrock.standalone.test.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;


@ChangeLog(order = "1")
public class MongockTestResource {


  @ChangeSet(author = "testuser", id = "test1", order = "01")
  public void testChangeSet_() {

    System.out.println("invoked 1");

  }

  @ChangeSet(author = "testuser", id = "test2", order = "02")
  public void testChangeSet2_() {

    System.out.println("invoked 2");

  }

  @ChangeSet(author = "testuser", id = "test4", order = "04")
  public void testChangeSet4_() {

    System.out.println("invoked 4");

  }

  @ChangeSet(author = "testuser", id = "test5", order = "05")
  public void testChangeSet5_(MongoDatabase mongoDatabase) {

    System.out.println("invoked 5 with mongoDatabase=" + mongoDatabase.toString());

  }

}
