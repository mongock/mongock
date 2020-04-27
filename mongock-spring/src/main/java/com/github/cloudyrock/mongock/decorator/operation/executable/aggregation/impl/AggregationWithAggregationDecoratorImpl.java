package com.github.cloudyrock.mongock.decorator.operation.executable.aggregation.impl;

import com.github.cloudyrock.mongock.decorator.util.DecoratorBase;
import com.github.cloudyrock.mongock.decorator.operation.executable.aggregation.AggregationWithAggregationDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;

public class AggregationWithAggregationDecoratorImpl<T>
    extends DecoratorBase<ExecutableAggregationOperation.AggregationWithAggregation<T>>
    implements AggregationWithAggregationDecorator<T> {

  public AggregationWithAggregationDecoratorImpl(ExecutableAggregationOperation.AggregationWithAggregation<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }
}
