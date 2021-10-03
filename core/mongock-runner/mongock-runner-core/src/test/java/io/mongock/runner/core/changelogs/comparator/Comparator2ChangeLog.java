package io.mongock.runner.core.changelogs.comparator;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;

@ChangeLog(order = "1")
public class Comparator2ChangeLog {


  @ChangeSet(author = "executor", id = "newChangeSet", order = "1")
  public void comparatorChangeSet1() {
  }


}
