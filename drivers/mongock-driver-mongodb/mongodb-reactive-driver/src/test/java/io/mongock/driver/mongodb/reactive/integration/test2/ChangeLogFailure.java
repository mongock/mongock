package io.mongock.driver.mongodb.reactive.integration.test2;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.reactivestreams.client.MongoDatabase;

@ChangeLog
public class ChangeLogFailure {

  @ChangeSet(author = "testuser", id = "id_duplicated", order = "00")
  public void method_0() {
    //System.out.println(ChangeLogFailure.class.getCanonicalName() + ".method_0()");
  }

  @ChangeSet(author = "testuser", id = "id_duplicated", order = "01")
  public void method_1() {
    //System.out.println(ChangeLogFailure.class.getCanonicalName() + ".method_1()");
  }


  @ChangeSet(author = "testuser", id = "ChangeLog1_4", order = "04")
  public void method_4(MongoDatabase mongoDatabase) {
    //System.out.println(ChangeLogFailure.class.getCanonicalName() + ".method_4(MongoDatabase mongoDatabase)\n\t\twith " + mongoDatabase.toString());
  }

}
