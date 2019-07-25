package com.github.cloudyrock.mongock.decorator;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Collation;

import java.util.concurrent.TimeUnit;

public interface AggregateIterableDecorator<T> extends MongoIterableDecorator<T>, AggregateIterable<T> {

  @Override
  AggregateIterable<T> getImpl();

  @Override
  default void toCollection() {

  }

  @Override
  default AggregateIterable<T> allowDiskUse(Boolean allowDiskUse) {
    return null;
  }

  @Override
  default AggregateIterable<T> batchSize(int batchSize) {
    return null;
  }

  @Override
  default AggregateIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
    return null;
  }

  @Override
  default AggregateIterable<T> useCursor(Boolean useCursor) {
    return null;
  }

  @Override
  default AggregateIterable<T> bypassDocumentValidation(Boolean bypassDocumentValidation) {
    return null;
  }

  @Override
  default AggregateIterable<T> collation(Collation collation) {
    return null;
  }
}
