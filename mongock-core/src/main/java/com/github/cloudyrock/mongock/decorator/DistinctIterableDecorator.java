package com.github.cloudyrock.mongock.decorator;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.model.Collation;
import org.bson.conversions.Bson;

import java.util.concurrent.TimeUnit;

public interface DistinctIterableDecorator<T> extends DistinctIterable<T>, MongoIterableDecorator<T> {

  @Override
  DistinctIterable<T> getImpl();

  @Override
  default DistinctIterable<T> filter(Bson filter) {
    return null;
  }

  @Override
  default DistinctIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
    return null;
  }

  @Override
  default DistinctIterable<T> batchSize(int batchSize) {
    return null;
  }

  @Override
  default DistinctIterable<T> collation(Collation collation) {
    return null;
  }
}
