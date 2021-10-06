package io.mongock.runner.springboot.profiles.integration;


import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import io.mongock.runner.springboot.util.CallVerifier;
import io.mongock.runner.springboot.util.TemplateForTest;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;


@ChangeLog(order = "01")
@Profile("profileIncluded1")
public class IntegrationProfiledChangerLog {

  // should be executed
  @Profile("profileIncluded1")
  @ChangeSet(author = "testuser", id = "testWithProfileIncluded1", order = "01")
  public void testWithProfileIncluded1() {
    System.out.println("invoked testWIthProfileIncluded1");
  }

  // should be executed
  @Profile("profileIncluded2")
  @ChangeSet(author = "testuser", id = "testWithProfileIncluded2", order = "02")
  public void testWithProfileIncluded2() {
    System.out.println("invoked testWIthProfileIncluded2");
  }

  // should not be executed
  @Profile("profileNotIncluded")
  @ChangeSet(author = "testuser", id = "testWithProfileINotIncluded", order = "03")
  public void testWithProfileINotIncluded() {
    System.out.println("invoked testWithProfileINotIncluded");
  }

  // should not be executed
  @Profile("default")
  @ChangeSet(author = "testuser", id = "testWithDefaultProfile", order = "04")
  public void testWithDefaultProfile() {
    System.out.println("invoked testWithDefaultProfile");
  }

  // should be executed
  @Profile({"profileIncluded1", "profileNotIncluded"})
  @ChangeSet(author = "testuser", id = "testWithProfileIncluded1OrProfileINotIncluded", order = "03")
  public void testWithProfileIncluded1OrProfileINotIncluded(
      Environment environment,
      CallVerifier callVerifier,
      TemplateForTest templateForTest) {
    callVerifier.counter++;
    System.out.println("invoked testWithProfileIncluded1OrProfileINotIncluded");
    if (environment == null) {
      throw new RuntimeException("Environment shouldn't be null in changeSet testWithProfileIncluded1OrProfileINotIncluded");
    }

    if (templateForTest == null) {
      throw new RuntimeException("Environment shouldn't be null in changeSet templateForTest");
    }

  }

}
