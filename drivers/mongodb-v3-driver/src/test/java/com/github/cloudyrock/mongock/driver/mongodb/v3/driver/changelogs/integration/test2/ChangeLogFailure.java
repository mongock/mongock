package com.github.cloudyrock.mongock.driver.mongodb.v3.driver.changelogs.integration.test2;

import com.mongodb.client.MongoDatabase;
import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;

@ChangeLog
public class ChangeLogFailure {

  @ChangeSet(author = "testuser", id = "id_duplicated", order = "00")
  public void method_0() {
    System.out.println(ChangeLogFailure.class.getCanonicalName() + ".method_0()");
  }

  @ChangeSet(author = "testuser", id = "id_duplicated", order = "01")
  public void method_1() {
    System.out.println(ChangeLogFailure.class.getCanonicalName() + ".method_1()");
  }


  @ChangeSet(author = "testuser", id = "ChangeLog1_4", order = "04")
  public void method_4(MongoDatabase mongoDatabase) {
    System.out.println(ChangeLogFailure.class.getCanonicalName() + ".method_4(MongoDatabase mongoDatabase)\n\t\twith " + mongoDatabase.toString());
  }

}
