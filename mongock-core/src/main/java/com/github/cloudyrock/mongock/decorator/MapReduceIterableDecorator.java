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
    getInvoker().invoke(() -> getImpl().toCollection());
  }

  @Override
  default MapReduceIterable<T> collectionName(String collectionName) {
    return getImpl().collectionName(collectionName);
  }

  @Override
  default MapReduceIterable<T> finalizeFunction(String finalizeFunction) {
    return getImpl().finalizeFunction(finalizeFunction);
  }

  @Override
  default MapReduceIterable<T> scope(Bson scope) {
    return getImpl().scope(scope);
  }

  @Override
  default MapReduceIterable<T> sort(Bson sort) {
    return getImpl().sort(sort);
  }

  @Override
  default MapReduceIterable<T> filter(Bson filter) {
    return getImpl().filter(filter);
  }

  @Override
  default MapReduceIterable<T> limit(int limit) {
    return getImpl().limit(limit);
  }

  @Override
  default MapReduceIterable<T> jsMode(boolean jsMode) {
    return getImpl().jsMode(jsMode);
  }

  @Override
  default MapReduceIterable<T> verbose(boolean verbose) {
    return getImpl().verbose(verbose);
  }

  @Override
  default MapReduceIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
    return getImpl().maxTime(maxTime, timeUnit);
  }

  @Override
  default MapReduceIterable<T> action(MapReduceAction action) {
    return getImpl().action(action);
  }

  @Override
  default MapReduceIterable<T> databaseName(String databaseName) {
    return getImpl().databaseName(databaseName);
  }

  @Override
  default MapReduceIterable<T> sharded(boolean sharded) {
    return getImpl().sharded(sharded);
  }

  @Override
  default MapReduceIterable<T> nonAtomic(boolean nonAtomic) {
    return getImpl().nonAtomic(nonAtomic);
  }

  @Override
  default MapReduceIterable<T> batchSize(int batchSize) {
    return getImpl().batchSize(batchSize);
  }

  @Override
  default MapReduceIterable<T> bypassDocumentValidation(Boolean bypassDocumentValidation) {
    return getImpl().bypassDocumentValidation(bypassDocumentValidation);
  }

  @Override
  default MapReduceIterable<T> collation(Collation collation) {
    return getImpl().collation(collation);
  }
}
