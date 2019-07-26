package com.github.cloudyrock.mongock.decorator;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Collation;

import java.util.concurrent.TimeUnit;

public interface AggregateIterableDecorator<T> extends MongoIterableDecorator<T>, AggregateIterable<T> {

  @Override
  AggregateIterable<T> getImpl();

  @Override
  default void toCollection() {
    getInvoker().invoke(() -> getImpl().toCollection());
  }

  @Override
  default AggregateIterable<T> allowDiskUse(Boolean allowDiskUse) {
    return getImpl().allowDiskUse(allowDiskUse);
  }

  @Override
  default AggregateIterable<T> batchSize(int batchSize) {
    return getImpl().batchSize(batchSize);
  }

  @Override
  default AggregateIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
    return getImpl().maxTime(maxTime, timeUnit);
  }

  @Override
  @Deprecated
  default AggregateIterable<T> useCursor(Boolean useCursor) {
    return getImpl().useCursor(useCursor);
  }

  @Override
  default AggregateIterable<T> bypassDocumentValidation(Boolean bypassDocumentValidation) {
    return getImpl().bypassDocumentValidation(bypassDocumentValidation);
  }

  @Override
  default AggregateIterable<T> collation(Collation collation) {
    return getImpl().collation(collation);
  }
}
