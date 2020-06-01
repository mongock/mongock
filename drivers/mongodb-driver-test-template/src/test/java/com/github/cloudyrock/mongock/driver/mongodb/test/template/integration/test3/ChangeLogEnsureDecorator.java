package com.github.cloudyrock.mongock.driver.mongodb.test.template.integration.test3;

import com.github.cloudyrock.mongock.driver.mongodb.test.template.util.CallVerifier;
import com.mongodb.client.MongoDatabase;
import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;
import org.junit.Assert;

@ChangeLog
public class ChangeLogEnsureDecorator {


  @ChangeSet(author = "testuser", id = "id_duplicated", order = "00")
  public void method(MongoDatabase mongodatabase, CallVerifier callVerifier) {
    Assert.assertEquals("MongoDataBaseDecoratorImpl".toLowerCase(), mongodatabase.getClass().getSimpleName().toLowerCase());
    callVerifier.increaseCounter();
  }

}
