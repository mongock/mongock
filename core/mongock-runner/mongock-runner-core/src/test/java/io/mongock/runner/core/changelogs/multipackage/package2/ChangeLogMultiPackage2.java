package io.mongock.runner.core.changelogs.multipackage.package2;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;

@ChangeLog(order = "3")
public class ChangeLogMultiPackage2 {

  @ChangeSet(author = "config", order = "3", id = "changeset_package2")
  public void changeSetPackage2() {
  }
}
