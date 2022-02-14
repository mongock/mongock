package io.mongock.driver.mongodb.reactive.integration.test3;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.mongock.driver.mongodb.reactive.util.CallVerifier;
import io.mongock.util.test.ReflectionUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ChangeLog
public class ChangeLogEnsureDecorator {


  @ChangeSet(author = "testuser", id = "id_duplicated", order = "00")
  public void method(MongoDatabase mongodatabase, CallVerifier callVerifier) {
    assertTrue(ReflectionUtils.isProxy(mongodatabase));
    callVerifier.increaseCounter();
  }

}
