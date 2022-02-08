package io.mongock.driver.mongodb.test.template.integration.test3;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import io.mongock.driver.mongodb.test.template.util.CallVerifier;
import com.mongodb.client.MongoDatabase;
import io.mongock.util.test.ReflectionUtils;
import org.junit.Assert;

@ChangeLog
public class ChangeLogEnsureDecorator {


  @ChangeSet(author = "testuser", id = "id_duplicated", order = "00")
  public void method(MongoDatabase mongodatabase, CallVerifier callVerifier) {
    Assert.assertTrue(ReflectionUtils.isProxy(mongodatabase));
    callVerifier.increaseCounter();
  }

}
