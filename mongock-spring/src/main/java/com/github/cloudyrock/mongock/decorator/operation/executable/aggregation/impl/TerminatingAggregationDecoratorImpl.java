package com.github.cloudyrock.mongock.decorator.operation.executable.aggregation.impl;

import com.github.cloudyrock.mongock.decorator.util.DecoratorBase;
import com.github.cloudyrock.mongock.decorator.operation.executable.aggregation.TerminatingAggregationDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;

public class TerminatingAggregationDecoratorImpl<T>
    extends DecoratorBase<ExecutableAggregationOperation.TerminatingAggregation<T>>
    implements TerminatingAggregationDecorator<T> {
  public TerminatingAggregationDecoratorImpl(ExecutableAggregationOperation.TerminatingAggregation<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }
}
