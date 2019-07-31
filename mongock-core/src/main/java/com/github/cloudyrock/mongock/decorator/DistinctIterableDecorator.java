package com.github.cloudyrock.mongock.decorator;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.model.Collation;
import org.bson.conversions.Bson;

import java.util.concurrent.TimeUnit;

public interface DistinctIterableDecorator<T> extends MongoIterableDecorator<T>, DistinctIterable<T> {

  @Override
  DistinctIterable<T> getImpl();

  @Override
  default DistinctIterable<T> filter(Bson filter) {
    return getImpl().filter(filter);
  }

  @Override
  default DistinctIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
    return getImpl().maxTime(maxTime, timeUnit);
  }

  @Override
  default DistinctIterable<T> batchSize(int batchSize) {
    return getImpl().batchSize(batchSize).batchSize(batchSize);
  }

  @Override
  default DistinctIterable<T> collation(Collation collation) {
    return getImpl().collation(collation);
  }
}
