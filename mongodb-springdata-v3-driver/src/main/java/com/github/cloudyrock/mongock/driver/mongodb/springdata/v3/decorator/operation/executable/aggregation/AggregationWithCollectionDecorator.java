package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.aggregation;

import io.mongock.driver.api.lock.guard.decorator.Invokable;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.aggregation.impl.AggregationWithAggregationDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;

public interface AggregationWithCollectionDecorator<T> extends Invokable, ExecutableAggregationOperation.AggregationWithCollection<T> {

  ExecutableAggregationOperation.AggregationWithCollection<T> getImpl();

  @Override
  default ExecutableAggregationOperation.AggregationWithAggregation<T> inCollection(String collection) {
    return new AggregationWithAggregationDecoratorImpl<>(getInvoker().invoke(()-> getImpl().inCollection(collection)), getInvoker());
  }
}
