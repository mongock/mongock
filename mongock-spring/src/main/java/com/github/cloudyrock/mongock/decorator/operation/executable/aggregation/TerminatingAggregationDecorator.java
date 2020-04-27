package com.github.cloudyrock.mongock.decorator.operation.executable.aggregation;

import com.github.cloudyrock.mongock.decorator.impl.CloseableIteratorDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.util.Invokable;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.util.CloseableIterator;

public interface TerminatingAggregationDecorator<T> extends Invokable, ExecutableAggregationOperation.TerminatingAggregation<T> {

  ExecutableAggregationOperation.TerminatingAggregation<T> getImpl();

  @Override
  default AggregationResults<T> all() {
    return getInvoker().invoke(()-> getImpl().all());
  }

  @Override
  default CloseableIterator<T> stream() {
    return new CloseableIteratorDecoratorImpl<>(getInvoker().invoke(()-> getImpl().stream()), getInvoker());
  }
}
