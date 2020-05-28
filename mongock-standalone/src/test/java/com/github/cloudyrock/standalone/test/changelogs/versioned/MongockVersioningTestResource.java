package com.github.cloudyrock.standalone.test.changelogs.versioned;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;

/**
 *
 * @since 27/07/2014
 */
@ChangeLog(order = "1")
public class MongockVersioningTestResource {

  @ChangeSet(author = "testuser", id = "testVersion1", order = "01", systemVersion = "0.1")
  public void testChangeSet1() {
    System.out.println("invoked 1");
  }

  @ChangeSet(author = "testuser", id = "testVersion2", order = "02", systemVersion = "0.1.1")
  public void testChangeSet2() {
    System.out.println("invoked 2");
  }

  @ChangeSet(author = "testuser", id = "testVersion3", order = "03", systemVersion = "0.2")
  public void testChangeSet3() {
    System.out.println("invoked 3");
  }

  @ChangeSet(author = "testuser", id = "testVersion4", order = "04", systemVersion = "0.2")
  public void testChangeSet4() {
    System.out.println("invoked 4");
  }

  @ChangeSet(author = "testuser", id = "testVersion5", order = "05", systemVersion = "1.0")
  public void testChangeSet5() {
    System.out.println("invoked 5");
  }

  @ChangeSet(author = "testuser", id = "testVersion6", order = "06", systemVersion = "1.0.1")
  public void testChangeSet6(MongoDatabase mongoDatabase) {
    System.out.println("invoked 6 with mongoDatabase=" + mongoDatabase.toString());
  }

  @ChangeSet(author = "testuser", id = "testVersion7", order = "07", systemVersion = "2")
  public void testChangeSet7(MongoDatabase mongoDatabase) {
    System.out.println("invoked 7 with mongoDatabase=" + mongoDatabase.toString());
  }

  @ChangeSet(author = "testuser", id = "testVersion8", order = "08", systemVersion = "2018.3")
  public void testChangeSet8() {
    System.out.println("invoked 8");
  }

  @ChangeSet(author = "testuser", id = "testVersion9", order = "09", systemVersion = "2019.1")
  public void testChangeSet9() {
    System.out.println("invoked 9");
  }

  @ChangeSet(author = "testuser", id = "testVersion10", order = "10", systemVersion = "2019.2")
  public void testChangeSet10() {
    System.out.println("invoked 10");
  }

}
