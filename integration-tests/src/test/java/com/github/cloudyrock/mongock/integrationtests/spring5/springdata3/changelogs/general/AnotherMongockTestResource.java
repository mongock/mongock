package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.general;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;

/**
 *
 * @since 30.07.14
 */
@ChangeLog(order = "2")
public class AnotherMongockTestResource {

  @ChangeSet(author = "testuser", id = "Btest1", order = "01")
  public void testChangeSet() {
    System.out.println("invoked B1");
  }

  @ChangeSet(author = "testuser", id = "Btest2", order = "02")
  public void testChangeSet2() {
    System.out.println("invoked B2");
  }

  @ChangeSet(author = "testuser", id = "Btest4", order = "04")
  public void testChangeSet4() {
    System.out.println("invoked B4");
  }

  @ChangeSet(author = "testuser", id = "Btest5", order = "05", runAlways = true)
  public void testChangeSetWithAlways() {
    System.out.println("invoked B5 with always");
  }

  @ChangeSet(author = "testuser", id = "Btest6", order = "06")
  public void testChangeSet6(MongoDatabase mongoDatabase) {
    System.out.println("invoked B6 with mongoDatabase=" + mongoDatabase.toString());
  }

}
