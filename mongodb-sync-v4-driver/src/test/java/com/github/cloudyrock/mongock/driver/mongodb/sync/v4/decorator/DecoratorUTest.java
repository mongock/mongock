package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.decorator;

import com.github.cloudyrock.mongock.driver.api.lock.LockManager;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvokerImpl;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.decorator.impl.MongoDataBaseDecoratorImpl;
import com.github.cloudyrock.mongock.test.util.decorator.DecoratorMethodFailure;
import com.github.cloudyrock.mongock.test.util.decorator.DecoratorTestCollection;
import com.github.cloudyrock.mongock.test.util.decorator.DecoratorValidator;
import com.mongodb.MongoNamespace;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.WriteConcern;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DecoratorUTest {


  private DecoratorTestCollection getDecoratorsToTest() {
    return new DecoratorTestCollection()
        .addDecorator(MongoDatabase.class, MongoDataBaseDecoratorImpl.class);

  }


  @Test
  public void allMethodsInDecoratorsShouldEnsureLockAndReturnDecoratorIfNotTerminatingOperations() {
    LockManager lockManager = Mockito.mock(LockManager.class);
    List<DecoratorMethodFailure> failedDecorators = new DecoratorValidator(
        getDecoratorsToTest(),
        getIgnoredTypes(),
        Collections.emptyList(),
        getInstancesMap(lockManager),
        lockManager)
        .checkAndReturnFailedDecorators();
    int size = failedDecorators.size();
    Assert.assertEquals(DecoratorMethodFailure.printErrorMessage(failedDecorators), 0, size);
  }

  private Map<Class, Object> getInstancesMap(LockManager lockManager) {
    Map<Class, Object> instancesMap = new HashMap<>();
    instancesMap.put(MongoDataBaseDecoratorImpl.class, new MongoDataBaseDecoratorImpl(Mockito.mock(MongoDatabase.class), new LockGuardInvokerImpl(lockManager)));
    return instancesMap;
  }

  private Collection<Class> getIgnoredTypes() {
    return new ArrayList<>(Arrays.asList(
        Document.class
        , BsonDocument.class
        , DeleteResult.class
        , UpdateResult.class
        , InsertOneResult.class
        , InsertManyResult.class
        , BulkWriteResult.class
        , CodecRegistry.class
        , ReadPreference.class
        , ReadConcern.class
        , WriteConcern.class
        , ServerCursor.class
        , ServerAddress.class
        , MongoNamespace.class
        , Optional.class
    ));
  }



}
