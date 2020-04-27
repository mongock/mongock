package com.github.cloudyrock.mongock.decorator.operation.executable.aggregation.impl;

import com.github.cloudyrock.mongock.decorator.util.DecoratorBase;
import com.github.cloudyrock.mongock.decorator.operation.executable.aggregation.AggregationWithCollectionDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;

public class AggregationWithCollectionDecoratorImpl<T>
    extends DecoratorBase<ExecutableAggregationOperation.AggregationWithCollection<T>>
    implements AggregationWithCollectionDecorator<T> {
  public AggregationWithCollectionDecoratorImpl(ExecutableAggregationOperation.AggregationWithCollection<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }
}
