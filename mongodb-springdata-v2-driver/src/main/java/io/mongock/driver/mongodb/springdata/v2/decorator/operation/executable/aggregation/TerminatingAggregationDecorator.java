package io.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.aggregation;

import io.mongock.driver.api.lock.guard.decorator.Invokable;
import io.mongock.driver.mongodb.springdata.v2.decorator.impl.CloseableIteratorDecoratorImpl;
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
