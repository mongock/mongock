package com.github.cloudyrock.mongock.decorator;

import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.MapReduceAction;
import org.bson.conversions.Bson;

import java.util.concurrent.TimeUnit;

public interface MapReduceIterableDecorator<T> extends MongoIterableDecorator<T>, MapReduceIterable<T> {
  @Override
  MapReduceIterable<T> getImpl();
  

  @Override
  default void toCollection() {
    getImpl().toCollection();
  }

  @Override
  default MapReduceIterable<T> collectionName(String collectionName) {
    return null;
  }

  @Override
  default MapReduceIterable<T> finalizeFunction(String finalizeFunction) {
    return null;
  }

  @Override
  default MapReduceIterable<T> scope(Bson scope) {
    return null;
  }

  @Override
  default MapReduceIterable<T> sort(Bson sort) {
    return null;
  }

  @Override
  default MapReduceIterable<T> filter(Bson filter) {
    return null;
  }

  @Override
  default MapReduceIterable<T> limit(int limit) {
    return null;
  }

  @Override
  default MapReduceIterable<T> jsMode(boolean jsMode) {
    return null;
  }

  @Override
  default MapReduceIterable<T> verbose(boolean verbose) {
    return null;
  }

  @Override
  default MapReduceIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
    return null;
  }

  @Override
  default MapReduceIterable<T> action(MapReduceAction action) {
    return null;
  }

  @Override
  default MapReduceIterable<T> databaseName(String databaseName) {
    return null;
  }

  @Override
  default MapReduceIterable<T> sharded(boolean sharded) {
    return null;
  }

  @Override
  default MapReduceIterable<T> nonAtomic(boolean nonAtomic) {
    return null;
  }

  @Override
  default MapReduceIterable<T> batchSize(int batchSize) {
    return null;
  }

  @Override
  default MapReduceIterable<T> bypassDocumentValidation(Boolean bypassDocumentValidation) {
    return null;
  }

  @Override
  default MapReduceIterable<T> collation(Collation collation) {
    return null;
  }
}
