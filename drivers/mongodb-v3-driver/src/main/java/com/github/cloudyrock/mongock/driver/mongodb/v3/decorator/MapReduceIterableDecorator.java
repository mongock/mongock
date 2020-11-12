package com.github.cloudyrock.mongock.driver.mongodb.v3.decorator;

import com.github.cloudyrock.mongock.driver.mongodb.v3.decorator.impl.MapReduceIterableDecoratorImpl;
import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.MapReduceAction;
import io.changock.migration.api.annotations.NonLockGuarded;
import org.bson.conversions.Bson;

import java.util.concurrent.TimeUnit;

public interface MapReduceIterableDecorator<T> extends MongoIterableDecorator<T>, MapReduceIterable<T> {

  @Override
  MapReduceIterable<T> getImpl();

  @Override
  default void toCollection() {
    getInvoker().invoke(() -> getImpl().toCollection());
  }

  @Override
  @NonLockGuarded
  default MapReduceIterable<T> collectionName(String collectionName) {
    return new MapReduceIterableDecoratorImpl<>(getImpl().collectionName(collectionName), getInvoker());
  }

  @Override
  @NonLockGuarded
  default MapReduceIterable<T> finalizeFunction(String finalizeFunction) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().finalizeFunction(finalizeFunction), getInvoker());
  }

  @Override
  @NonLockGuarded
  default MapReduceIterable<T> scope(Bson scope) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().scope(scope), getInvoker());
  }

  @Override
  @NonLockGuarded
  default MapReduceIterable<T> sort(Bson sort) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().sort(sort), getInvoker());
  }

  @Override
  @NonLockGuarded
  default MapReduceIterable<T> filter(Bson filter) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().filter(filter), getInvoker());
  }

  @Override
  @NonLockGuarded
  default MapReduceIterable<T> limit(int limit) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().limit(limit), getInvoker());
  }

  @Override
  @NonLockGuarded
  default MapReduceIterable<T> jsMode(boolean jsMode) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().jsMode(jsMode), getInvoker());
  }

  @Override
  @NonLockGuarded
  default MapReduceIterable<T> verbose(boolean verbose) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().verbose(verbose), getInvoker());
  }

  @Override
  @NonLockGuarded
  default MapReduceIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().maxTime(maxTime, timeUnit), getInvoker());
  }

  @Override
  @NonLockGuarded
  default MapReduceIterable<T> action(MapReduceAction action) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().action(action), getInvoker());
  }

  @Override
  @NonLockGuarded
  default MapReduceIterable<T> databaseName(String databaseName) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().databaseName(databaseName), getInvoker());
  }

  @Override
  @NonLockGuarded
  default MapReduceIterable<T> sharded(boolean sharded) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().sharded(sharded), getInvoker());
  }

  @Override
  @NonLockGuarded
  default MapReduceIterable<T> nonAtomic(boolean nonAtomic) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().nonAtomic(nonAtomic), getInvoker());
  }

  @Override
  @NonLockGuarded
  default MapReduceIterable<T> batchSize(int batchSize) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().batchSize(batchSize), getInvoker());
  }

  @Override
  @NonLockGuarded
  default MapReduceIterable<T> bypassDocumentValidation(Boolean bypassDocumentValidation) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().bypassDocumentValidation(bypassDocumentValidation), getInvoker());
  }

  @Override
  @NonLockGuarded
  default MapReduceIterable<T> collation(Collation collation) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().collation(collation), getInvoker());
  }
}
