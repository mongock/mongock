package io.mongock.runner.core.changelogs.executor.withInterfaceParameter;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import io.changock.migration.api.annotations.NonLockGuarded;
import io.mongock.runner.core.util.InterfaceDependency;

@ChangeLog(order = "0")
public class ChangeLogWithInterfaceParameter {


  @ChangeSet(author = "executor", id = "withInterfaceParameter", order = "1")
  public void withInterfaceParameter(InterfaceDependency dependency) {
    dependency.getValue();
  }

  @ChangeSet(author = "executor", id = "withInterfaceParameter2", order = "2")
  public void withInterfaceParameter2(InterfaceDependency dependency) {
    dependency.getInstance().getValue();
  }


  @ChangeSet(author = "executor", id = "withNonLockGuardedParameter", order = "3")
  public void withNonLockGuardedParameter(@NonLockGuarded InterfaceDependency dependency) {
    dependency.getInstance().getValue();
  }


}
