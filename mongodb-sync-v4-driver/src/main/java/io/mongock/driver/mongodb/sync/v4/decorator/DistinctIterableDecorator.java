package io.mongock.driver.mongodb.sync.v4.decorator;

import com.github.cloudyrock.mongock.NonLockGuarded;
import io.mongock.driver.mongodb.sync.v4.decorator.impl.DistinctIterableDecoratorImpl;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.model.Collation;
import org.bson.conversions.Bson;

import java.util.concurrent.TimeUnit;

public interface DistinctIterableDecorator<T> extends MongoIterableDecorator<T>, DistinctIterable<T> {

  @Override
  DistinctIterable<T> getImpl();

  @Override
  @NonLockGuarded
  default DistinctIterable<T> filter(Bson filter) {
    return new DistinctIterableDecoratorImpl<>(getImpl().filter(filter), getInvoker());
  }

  @Override
  @NonLockGuarded
  default DistinctIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
    return new DistinctIterableDecoratorImpl<>(getImpl().maxTime(maxTime, timeUnit), getInvoker());
  }

  @Override
  @NonLockGuarded
  default DistinctIterable<T> batchSize(int batchSize) {
    return new DistinctIterableDecoratorImpl<>(getImpl().batchSize(batchSize), getInvoker());
  }

  @Override
  @NonLockGuarded
  default DistinctIterable<T> collation(Collation collation) {
    return new DistinctIterableDecoratorImpl<>(getImpl().collation(collation), getInvoker());
  }
}
