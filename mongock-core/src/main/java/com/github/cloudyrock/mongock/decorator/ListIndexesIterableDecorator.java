package com.github.cloudyrock.mongock.decorator;

import com.mongodb.Block;
import com.mongodb.Function;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoIterable;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public interface ListIndexesIterableDecorator<T> extends ListIndexesIterable<T>, MongoIterableDecorator<T> {
  @Override
  ListIndexesIterable<T> getImpl();

  @Override
  default ListIndexesIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
    return null;
  }

  @Override
  default ListIndexesIterable<T> batchSize(int batchSize) {
    return null;
  }
}
