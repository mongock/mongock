package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.aggregation;

import io.mongock.driver.api.lock.guard.decorator.Invokable;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.aggregation.impl.TerminatingAggregationDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

public interface AggregationWithAggregationDecorator<T> extends Invokable, ExecutableAggregationOperation.AggregationWithAggregation<T> {

  ExecutableAggregationOperation.AggregationWithAggregation<T> getImpl();

  @Override
  default ExecutableAggregationOperation.TerminatingAggregation<T> by(Aggregation aggregation) {
    return new TerminatingAggregationDecoratorImpl<>(getInvoker().invoke(()-> getImpl().by(aggregation)), getInvoker());
  }
}
