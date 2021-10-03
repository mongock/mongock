package io.mongock.runner.springboot.profiles.enseuredecorators;


import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import io.mongock.runner.springboot.util.CallVerifier;
import io.mongock.runner.springboot.util.TemplateForTestImpl;
import io.mongock.runner.springboot.util.TemplateForTestImplChild;


@ChangeLog(order = "01")
public class EnsureDecoratorChangerLog {

  @ChangeSet(author = "testuser", id = "ensureDecoratorChangeSet", order = "01")
  public void ensureDecoratorChangeSet(
      CallVerifier callVerifier,
      TemplateForTestImpl templateForTest) {
    callVerifier.counter++;
    System.out.println("invoked ensureDecoratorChangeSet");

    if (templateForTest == null) {
      throw new RuntimeException("Must pass dependency");
    }
    if (!TemplateForTestImplChild.class.isAssignableFrom(templateForTest.getClass())) {
      throw new RuntimeException("Must prioritise pass connector dependency");
    }

  }

}
