package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.aggregation.impl;

import io.changock.driver.api.lock.guard.decorator.DecoratorBase;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.aggregation.AggregationWithCollectionDecorator;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;

public class AggregationWithCollectionDecoratorImpl<T>
    extends DecoratorBase<ExecutableAggregationOperation.AggregationWithCollection<T>>
    implements AggregationWithCollectionDecorator<T> {
  public AggregationWithCollectionDecoratorImpl(ExecutableAggregationOperation.AggregationWithCollection<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
