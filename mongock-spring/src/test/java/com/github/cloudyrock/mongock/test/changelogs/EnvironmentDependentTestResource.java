package com.github.cloudyrock.mongock.test.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import io.changock.driver.mongo.springdata.v3.driver.decorator.impl.MongockTemplate;
import org.springframework.core.env.Environment;

@ChangeLog(order = "3")
public class EnvironmentDependentTestResource {
  @ChangeSet(author = "testuser", id = "Envtest1", order = "01")
  public void testChangeSet7WithEnvironment(MongockTemplate template, Environment env) {
    if(env == null) {
      throw new NullPointerException("Environment in method " + EnvironmentDependentTestResource.class.getName() + ".testChangeSet7WithEnvironment is null");
    }
    if(template == null) {
      throw new NullPointerException("MongoTemplate in method " + EnvironmentDependentTestResource.class.getName() + ".testChangeSet7WithEnvironment is null");
    }
    System.out.println("invoked Envtest1 with mongotemplate=" + template.toString() + " and Environment " + env);
  }
}
