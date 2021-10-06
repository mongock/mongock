package io.mongock.runner.springboot.profiles.withinterfaceparameter;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import io.changock.migration.api.annotations.NonLockGuarded;
import io.mongock.runner.springboot.util.ClassNotInterfaced;
import io.mongock.runner.springboot.util.InterfaceDependency;

@ChangeLog(order = "0")
public class ChangeLogWithInterfaceParameter {


  @ChangeSet(author = "executor", id = "withInterfaceParameter", order = "1")
  public void withInterfaceParameter(InterfaceDependency dependency) {
    dependency.getValue();
  }

  @ChangeSet(author = "executor", id = "withInterfaceParameter2", order = "1")
  public void withInterfaceParameter2(InterfaceDependency dependency) {
    dependency.getInstance().getValue();
  }


  @ChangeSet(author = "executor", id = "withClassNotInterfacedParameter", order = "1")
  public void withClassNotInterfacedParameter(ClassNotInterfaced dependency) {
    //empty method
  }


  @ChangeSet(author = "executor", id = "withNonLockGuardedParameter", order = "3")
  public void withNonLockGuardedParameter(@NonLockGuarded InterfaceDependency dependency) {
    dependency.getInstance().getValue();
  }


}
