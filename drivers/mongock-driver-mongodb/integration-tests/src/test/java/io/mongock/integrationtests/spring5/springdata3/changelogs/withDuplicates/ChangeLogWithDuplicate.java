package io.mongock.integrationtests.spring5.springdata3.changelogs.withDuplicates;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;

@ChangeLog
public class ChangeLogWithDuplicate {
  @ChangeSet(author = "testuser", id = "Btest1", order = "01")
  public void testChangeSet() {
  }

  @ChangeSet(author = "testuser", id = "Btest2", order = "02")
  public void testChangeSet2() {
  }

  @ChangeSet(author = "testuser", id = "Btest2", order = "03")
  public void testChangeSet3() {
  }
}
