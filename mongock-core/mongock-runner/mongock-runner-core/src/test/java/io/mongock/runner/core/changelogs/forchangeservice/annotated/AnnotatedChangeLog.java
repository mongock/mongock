package io.mongock.runner.core.changelogs.forchangeservice.annotated;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;

@ChangeLog(order = "3")
public class AnnotatedChangeLog {

  @ChangeSet(id = "AnnotatedChangeLog",author = "mongock_test", order = "1", runAlways = true, systemVersion = "1")
  public void changeSet() {
  }


}
