package io.mongock.runner.springboot.profiles.defaultprofiled;


import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import org.springframework.context.annotation.Profile;


@ChangeLog(order = "01")
@Profile("default")
public class DefaultProfiledChangerLog {

  @Profile("default")
  @ChangeSet(author = "testuser", id = "default-profiled", order = "01")
  public void defaultProfiled() {
    System.out.println("invoked Pdev1");
  }

  @ChangeSet(author = "testuser", id = "no-profiled", order = "02")
  public void noProfiled() {
    System.out.println("invoked Pdev2");
  }


}
