package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.decorator.impl.AggregateIterableDecoratorImpl;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Collation;
import org.bson.conversions.Bson;

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
    return new AggregateIterableDecoratorImpl<>(getImpl().allowDiskUse(allowDiskUse), getInvoker());
  }

  @Override
  default AggregateIterable<T> batchSize(int batchSize) {
    return new AggregateIterableDecoratorImpl<>(getImpl().batchSize(batchSize), getInvoker());
  }

  @Override
  default AggregateIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
    return new AggregateIterableDecoratorImpl<>(getImpl().maxTime(maxTime, timeUnit), getInvoker());
  }

  @Override
  @Deprecated
  default AggregateIterable<T> useCursor(Boolean useCursor) {
    return new AggregateIterableDecoratorImpl<>(getImpl().useCursor(useCursor), getInvoker());
  }

  @Override
  default AggregateIterable<T> bypassDocumentValidation(Boolean bypassDocumentValidation) {
    return new AggregateIterableDecoratorImpl<>(getImpl().bypassDocumentValidation(bypassDocumentValidation), getInvoker());
  }

  @Override
  default AggregateIterable<T> collation(Collation collation) {
    return new AggregateIterableDecoratorImpl<>(getImpl().collation(collation), getInvoker());
  }


  @Override
  default AggregateIterable<T> maxAwaitTime(long l, TimeUnit timeUnit) {
    return new AggregateIterableDecoratorImpl<>(getImpl().maxAwaitTime(l, timeUnit), getInvoker());
  }

  @Override
  default AggregateIterable<T> comment(String s) {
    return new AggregateIterableDecoratorImpl<>(getImpl().comment(s), getInvoker());
  }

  @Override
  default AggregateIterable<T> hint(Bson bson) {
    return new AggregateIterableDecoratorImpl<>(getImpl().hint(bson), getInvoker());
  }
}
