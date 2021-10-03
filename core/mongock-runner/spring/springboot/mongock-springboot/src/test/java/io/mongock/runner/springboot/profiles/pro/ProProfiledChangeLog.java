package io.mongock.runner.springboot.profiles.pro;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import org.springframework.context.annotation.Profile;


@ChangeLog
public class ProProfiledChangeLog {
  @ChangeSet(author = "testuser", id = "no-profiled", order = "01")
  public void noProfiledMethod() {
    System.out.println("invoked Pdev1");
  }

  @Profile("pro")
  @ChangeSet(author = "testuser", id = "pro-profiled", order = "04")
  public void proProfiledMethod() {
    System.out.println("invoked Pdev4");
  }

  @Profile("!pro")
  @ChangeSet(author = "testuser", id = "no-pro-profiled", order = "05", runAlways = true)
  public void noProProfiledMethod() {
    System.out.println("invoked Pdev5");
  }
}
