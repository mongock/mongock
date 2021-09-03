package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.aggregation;

import io.mongock.driver.api.lock.guard.decorator.Invokable;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;

public interface ExecutableAggregationDecorator<T> extends
    Invokable,
    ExecutableAggregationOperation.ExecutableAggregation<T>,
    AggregationWithCollectionDecorator<T>,
    AggregationWithAggregationDecorator<T> {

  ExecutableAggregationOperation.ExecutableAggregation<T> getImpl();
}
