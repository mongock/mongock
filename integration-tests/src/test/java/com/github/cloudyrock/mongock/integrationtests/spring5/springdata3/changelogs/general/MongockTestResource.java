package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.general;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;

/**
 *
 * @since 27/07/2014
 */
@ChangeLog(order = "1")
public class MongockTestResource {

  @ChangeSet(author = "testuser", id = "test1", order = "01", failFast = false)
  public void testChangeSet() {

    throw new RuntimeException();
//    System.out.println("invoked 1");

  }

  @ChangeSet(author = "testuser", id = "test2", order = "02")
  public void testChangeSet2() {

    System.out.println("invoked 2");

  }

  @ChangeSet(author = "testuser", id = "test4", order = "04")
  public void testChangeSet4() {

    System.out.println("invoked 4");

  }

  @ChangeSet(author = "testuser", id = "test5", order = "05")
  public void testChangeSet5(MongoDatabase mongoDatabase) {

    System.out.println("invoked 5 with mongoDatabase=" + mongoDatabase.toString());

  }

}
