package com.github.cloudyrock.mongock.test.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;

import org.jongo.Jongo;

/**
 *
 * @since 27/07/2014
 */
@ChangeLog(order = "1")
public class JongoMongockTestResource {

  @ChangeSet(author = "testuser", id = "test1", order = "01")
  public void testChangeSet() {

    System.out.println("invoked 1");
  }

  @ChangeSet(author = "testuser", id = "test2", order = "02")
  public void testChangeSet2(Jongo jongo) {

    System.out.println("invoked 2 with jongo=" + jongo.toString());
  }

  @ChangeSet(author = "testuser", id = "test3", order = "03", runAlways = true)
  public void testChangeSetWithAlways(Jongo jongo) {

    System.out.println("invoked 3 with always + jongo=" + jongo.getDatabase());
  }

  @ChangeSet(author = "testuser", id = "test4", order = "04")
  public void testChangeSet4() {

    System.out.println("invoked 4");
  }
}
