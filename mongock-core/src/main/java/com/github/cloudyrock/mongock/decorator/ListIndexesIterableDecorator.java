package com.github.cloudyrock.mongock.decorator;

import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCursor;

import java.util.concurrent.TimeUnit;

public interface ListIndexesIterableDecorator<T> extends ListIndexesIterable<T>, MongoIterableDecorator<T> {
  @Override
  ListIndexesIterable<T> getImpl();

  @Override
  default ListIndexesIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
    return getImpl().maxTime(maxTime, timeUnit);
  }

  @Override
  default ListIndexesIterable<T> batchSize(int batchSize) {
    return getImpl().batchSize(batchSize);
  }

}
