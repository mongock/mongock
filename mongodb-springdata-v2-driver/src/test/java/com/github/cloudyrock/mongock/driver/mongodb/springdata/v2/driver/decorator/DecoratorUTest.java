package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.driver.decorator;

import com.github.cloudyrock.mongock.driver.api.lock.LockManager;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvokerImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.impl.MongockTemplate;
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
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SessionScoped;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.script.NamedMongoScript;
import org.springframework.data.util.CloseableIterator;

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
        .addDecorator(MongoOperations.class, MongockTemplate.class);

  }


  @Test
  public void allMethodsInDecoratorsShouldEnsureLockAndReturnDecoratorIfNotTerminatingOperations() {
    LockManager lockManager = Mockito.mock(LockManager.class);
    List<DecoratorMethodFailure> failedDecorators = new DecoratorValidator(
        getDecoratorsToTest(),
        getIgnoredTypes(),
        Collections.singletonList(MongockTemplate.class),
        getInstancesMap(lockManager),
        lockManager)
        .checkAndReturnFailedDecorators();
    int size = failedDecorators.size();
    Assert.assertEquals(DecoratorMethodFailure.printErrorMessage(failedDecorators), 0, size);
  }

  private Map<Class, Object> getInstancesMap(LockManager lockManager) {
    Map<Class, Object> instancesMap = new HashMap<>();
    instancesMap.put(MongockTemplate.class, new MongockTemplate(Mockito.mock(MongoTemplate.class), new LockGuardInvokerImpl(lockManager)));
    return instancesMap;
  }

  private Collection<Class> getIgnoredTypes() {
    return new ArrayList<>(Arrays.asList(
        Document.class
        , MongoConverter.class
        , GroupByResults.class
        , DeleteResult.class
        , AggregationResults.class
        , GeoResults.class
        , GeoResult.class
        , UpdateResult.class
        , MapReduceResults.class
        , CloseableIterator.class
        , MongoNamespace.class
        , CodecRegistry.class
        , ReadPreference.class
        , ReadConcern.class
        , WriteConcern.class
        , BulkWriteResult.class
        , NamedMongoScript.class
        , BsonDocument.class
        , ServerCursor.class
        , ServerAddress.class
        , Optional.class

        , SessionScoped.class// TODO remove this
    ));
  }


}
