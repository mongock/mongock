package com.github.cloudyrock.mongock.decorator.operation.executable.aggregation;

import com.github.cloudyrock.mongock.decorator.util.Invokable;
import com.github.cloudyrock.mongock.decorator.operation.executable.aggregation.impl.TerminatingAggregationDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

public interface AggregationWithAggregationDecorator<T> extends Invokable, ExecutableAggregationOperation.AggregationWithAggregation<T> {

  ExecutableAggregationOperation.AggregationWithAggregation<T> getImpl();

  @Override
  default ExecutableAggregationOperation.TerminatingAggregation<T> by(Aggregation aggregation) {
    return new TerminatingAggregationDecoratorImpl<>(getInvoker().invoke(()-> getImpl().by(aggregation)), getInvoker());
  }
}
