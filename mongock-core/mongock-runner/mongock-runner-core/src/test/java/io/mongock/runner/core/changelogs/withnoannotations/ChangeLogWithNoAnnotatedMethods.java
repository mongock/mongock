package io.mongock.runner.core.changelogs.withnoannotations;

import com.github.cloudyrock.mongock.ChangeLog;

@ChangeLog(order = "3")
public class ChangeLogWithNoAnnotatedMethods {

  public void method() {
  }

}
