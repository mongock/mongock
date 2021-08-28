package io.mongock.driver.mongodb.v3.decorator;

import com.github.cloudyrock.mongock.NonLockGuarded;
import io.mongock.driver.mongodb.v3.decorator.impl.AggregateIterableDecoratorImpl;
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
  @NonLockGuarded
  default AggregateIterable<T> allowDiskUse(Boolean allowDiskUse) {
    return new AggregateIterableDecoratorImpl<>(getImpl().allowDiskUse(allowDiskUse), getInvoker());
  }

  @Override
  @NonLockGuarded
  default AggregateIterable<T> batchSize(int batchSize) {
    return new AggregateIterableDecoratorImpl<>(getImpl().batchSize(batchSize), getInvoker());
  }

  @Override
  @NonLockGuarded
  default AggregateIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
    return new AggregateIterableDecoratorImpl<>(getImpl().maxTime(maxTime, timeUnit), getInvoker());
  }

  @Override
  @Deprecated
  @NonLockGuarded
  default AggregateIterable<T> useCursor(Boolean useCursor) {
    return new AggregateIterableDecoratorImpl<>(getImpl().useCursor(useCursor), getInvoker());
  }

  @Override
  @NonLockGuarded
  default AggregateIterable<T> bypassDocumentValidation(Boolean bypassDocumentValidation) {
    return new AggregateIterableDecoratorImpl<>(getImpl().bypassDocumentValidation(bypassDocumentValidation), getInvoker());
  }

  @Override
  @NonLockGuarded
  default AggregateIterable<T> collation(Collation collation) {
    return new AggregateIterableDecoratorImpl<>(getImpl().collation(collation), getInvoker());
  }


  @Override
  @NonLockGuarded
  default AggregateIterable<T> maxAwaitTime(long l, TimeUnit timeUnit) {
    return new AggregateIterableDecoratorImpl<>(getImpl().maxAwaitTime(l, timeUnit), getInvoker());
  }

  @Override
  @NonLockGuarded
  default AggregateIterable<T> comment(String s) {
    return new AggregateIterableDecoratorImpl<>(getImpl().comment(s), getInvoker());
  }

  @Override
  @NonLockGuarded
  default AggregateIterable<T> hint(Bson bson) {
    return new AggregateIterableDecoratorImpl<>(getImpl().hint(bson), getInvoker());
  }
}
