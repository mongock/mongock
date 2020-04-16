package com.github.cloudyrock.mongock.decorator.operation.executable.aggregation;

import com.github.cloudyrock.mongock.decorator.util.Invokable;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;

public interface ExecutableAggregationDecorator<T> extends
    Invokable,
    ExecutableAggregationOperation.ExecutableAggregation<T>,
    AggregationWithCollectionDecorator<T>,
    AggregationWithAggregationDecorator<T> {

  ExecutableAggregationOperation.ExecutableAggregation<T> getImpl();
}
