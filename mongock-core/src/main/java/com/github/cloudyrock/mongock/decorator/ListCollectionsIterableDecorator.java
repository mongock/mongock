package com.github.cloudyrock.mongock.decorator;

import com.mongodb.client.ListCollectionsIterable;
import org.bson.conversions.Bson;

import java.util.concurrent.TimeUnit;

public interface ListCollectionsIterableDecorator<T> extends MongoIterableDecorator<T>, ListCollectionsIterable<T> {

  @Override
  ListCollectionsIterable<T> getImpl();

  @Override
  default ListCollectionsIterable<T> filter(Bson filter) {
    return null;
  }

  @Override
  default ListCollectionsIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
    return null;
  }

  @Override
  default ListCollectionsIterable<T> batchSize(int batchSize) {
    return null;
  }
}
