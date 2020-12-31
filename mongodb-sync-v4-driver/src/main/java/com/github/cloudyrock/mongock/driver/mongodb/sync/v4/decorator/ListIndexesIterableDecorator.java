package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.decorator;

import io.changock.migration.api.annotations.NonLockGuarded;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.decorator.impl.ListIndexesIterableDecoratorImpl;
import com.mongodb.client.ListIndexesIterable;

import java.util.concurrent.TimeUnit;

public interface ListIndexesIterableDecorator<T> extends ListIndexesIterable<T>, MongoIterableDecorator<T> {
  @Override
  ListIndexesIterable<T> getImpl();

  @Override
  @NonLockGuarded
  default ListIndexesIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
    return new ListIndexesIterableDecoratorImpl<>(getImpl().maxTime(maxTime, timeUnit), getInvoker());
  }

  @Override
  @NonLockGuarded
  default ListIndexesIterable<T> batchSize(int batchSize) {
    return new ListIndexesIterableDecoratorImpl<>( getImpl().batchSize(batchSize), getInvoker());
  }

}
