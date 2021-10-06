package io.mongock.runner.core.changelogs.multipackage.package1;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;

@ChangeLog(order = "1")
public class ChangeLogMultiPackage1 {

  @ChangeSet(author = "config", order = "1", id = "changeset_package1")
  public void changeSetPackage1() {
  }
}
