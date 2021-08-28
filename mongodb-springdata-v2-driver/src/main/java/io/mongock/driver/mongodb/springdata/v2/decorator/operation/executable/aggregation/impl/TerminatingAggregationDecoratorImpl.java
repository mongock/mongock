package io.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.aggregation.impl;

import io.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.aggregation.TerminatingAggregationDecorator;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;

public class TerminatingAggregationDecoratorImpl<T>
    extends DecoratorBase<ExecutableAggregationOperation.TerminatingAggregation<T>>
    implements TerminatingAggregationDecorator<T> {
  public TerminatingAggregationDecoratorImpl(ExecutableAggregationOperation.TerminatingAggregation<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
