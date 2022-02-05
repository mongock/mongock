package io.mongock.driver.mongodb.async.integration.test3;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.mongock.driver.mongodb.async.util.CallVerifier;
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
