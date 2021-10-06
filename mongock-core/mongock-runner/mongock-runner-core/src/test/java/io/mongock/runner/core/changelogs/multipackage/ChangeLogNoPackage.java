package io.mongock.runner.core.changelogs.multipackage;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;

@ChangeLog(order = "2")
public class ChangeLogNoPackage {

  @ChangeSet(author = "config", order = "2", id = "no_package")
  public void noPackage() {
  }

  @ChangeSet(author = "config", order = "2", id = "no_package_2")
  public void noPackage2() {
  }
}
