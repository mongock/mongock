package com.github.cloudyrock.mongock.decorator;

import com.mongodb.client.ListCollectionsIterable;
import org.bson.conversions.Bson;

import java.util.concurrent.TimeUnit;

public interface ListCollectionsIterableDecorator<T> extends MongoIterableDecorator<T>, ListCollectionsIterable<T> {

  @Override
  ListCollectionsIterable<T> getImpl();

  @Override
  default ListCollectionsIterable<T> filter(Bson filter) {
    return getImpl().filter(filter);
  }

  @Override
  default ListCollectionsIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
    return getImpl().maxTime(maxTime, timeUnit);
  }

  @Override
  default ListCollectionsIterable<T> batchSize(int batchSize) {
    return getImpl().batchSize(batchSize);
  }
}
