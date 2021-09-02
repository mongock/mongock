package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.aggregation.impl;

import io.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.aggregation.ExecutableAggregationDecorator;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;

public class ExecutableAggregationDecoratorImpl<T>
    extends DecoratorBase<ExecutableAggregationOperation.ExecutableAggregation<T>>
    implements ExecutableAggregationDecorator<T> {
  public ExecutableAggregationDecoratorImpl(ExecutableAggregationOperation.ExecutableAggregation<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
