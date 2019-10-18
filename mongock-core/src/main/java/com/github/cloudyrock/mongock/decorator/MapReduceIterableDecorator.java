package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.decorator.impl.MapReduceIterableDecoratorImpl;
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
    getInvoker().invoke(() -> getImpl().toCollection());
  }

  @Override
  default MapReduceIterable<T> collectionName(String collectionName) {
    return new MapReduceIterableDecoratorImpl<>(getImpl().collectionName(collectionName), getInvoker());
  }

  @Override
  default MapReduceIterable<T> finalizeFunction(String finalizeFunction) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().finalizeFunction(finalizeFunction), getInvoker());
  }

  @Override
  default MapReduceIterable<T> scope(Bson scope) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().scope(scope), getInvoker());
  }

  @Override
  default MapReduceIterable<T> sort(Bson sort) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().sort(sort), getInvoker());
  }

  @Override
  default MapReduceIterable<T> filter(Bson filter) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().filter(filter), getInvoker());
  }

  @Override
  default MapReduceIterable<T> limit(int limit) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().limit(limit), getInvoker());
  }

  @Override
  default MapReduceIterable<T> jsMode(boolean jsMode) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().jsMode(jsMode), getInvoker());
  }

  @Override
  default MapReduceIterable<T> verbose(boolean verbose) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().verbose(verbose), getInvoker());
  }

  @Override
  default MapReduceIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().maxTime(maxTime, timeUnit), getInvoker());
  }

  @Override
  default MapReduceIterable<T> action(MapReduceAction action) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().action(action), getInvoker());
  }

  @Override
  default MapReduceIterable<T> databaseName(String databaseName) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().databaseName(databaseName), getInvoker());
  }

  @Override
  default MapReduceIterable<T> sharded(boolean sharded) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().sharded(sharded), getInvoker());
  }

  @Override
  default MapReduceIterable<T> nonAtomic(boolean nonAtomic) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().nonAtomic(nonAtomic), getInvoker());
  }

  @Override
  default MapReduceIterable<T> batchSize(int batchSize) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().batchSize(batchSize), getInvoker());
  }

  @Override
  default MapReduceIterable<T> bypassDocumentValidation(Boolean bypassDocumentValidation) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().bypassDocumentValidation(bypassDocumentValidation), getInvoker());
  }

  @Override
  default MapReduceIterable<T> collation(Collation collation) {
    return new MapReduceIterableDecoratorImpl<>( getImpl().collation(collation), getInvoker());
  }
}
