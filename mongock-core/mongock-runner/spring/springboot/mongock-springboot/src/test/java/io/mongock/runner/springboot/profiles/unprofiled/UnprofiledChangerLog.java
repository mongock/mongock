package io.mongock.runner.springboot.profiles.unprofiled;


import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;


@ChangeLog(order = "01")
public class UnprofiledChangerLog {

  @ChangeSet(author = "testuser", id = "no-profiled", order = "01")
  public void noProfiled() {
    System.out.println("invoked Pdev1");
  }

}
