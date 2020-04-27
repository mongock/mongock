package com.github.cloudyrock.mongock.decorator.operation.executable.aggregation.impl;

import com.github.cloudyrock.mongock.decorator.util.DecoratorBase;
import com.github.cloudyrock.mongock.decorator.operation.executable.aggregation.ExecutableAggregationDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;

public class ExecutableAggregationDecoratorImpl<T>
    extends DecoratorBase<ExecutableAggregationOperation.ExecutableAggregation<T>>
    implements ExecutableAggregationDecorator<T> {
  public ExecutableAggregationDecoratorImpl(ExecutableAggregationOperation.ExecutableAggregation<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }
}
