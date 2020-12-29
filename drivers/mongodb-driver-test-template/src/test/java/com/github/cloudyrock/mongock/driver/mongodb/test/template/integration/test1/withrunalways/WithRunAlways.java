package com.github.cloudyrock.mongock.driver.mongodb.test.template.integration.test1.withrunalways;

import com.mongodb.client.MongoDatabase;
import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;

@ChangeLog
public class WithRunAlways {

  @ChangeSet(author = "testuser", id = "method_0", order = "00", runAlways = true)
  public void method_0() {
    System.out.println(WithRunAlways.class.getCanonicalName() + ".method_0()");
  }

  @ChangeSet(author = "testuser", id = "method_1", order = "01")
  public void method_1() {
    System.out.println(WithRunAlways.class.getCanonicalName() + ".method_1()");
  }

  @ChangeSet(author = "testuser", id = "method_2", order = "02")
  public void method_2() {
    System.out.println(WithRunAlways.class.getCanonicalName() + ".method_2()");
  }

  @ChangeSet(author = "testuser", id = "method_3", order = "03", runAlways = true)
  public void method_3() {
    System.out.println(WithRunAlways.class.getCanonicalName() + ".method_3()");
  }

  @ChangeSet(author = "testuser", id = "method_4", order = "04")
  public void method_4(MongoDatabase mongoDatabase) {
    System.out.println(WithRunAlways.class.getCanonicalName() + ".method_4(MongoDatabase mongoDatabase)\n\t\twith " + mongoDatabase.toString());
  }

}
