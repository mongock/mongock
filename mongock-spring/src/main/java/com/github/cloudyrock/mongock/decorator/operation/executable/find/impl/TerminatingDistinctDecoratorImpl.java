package com.github.cloudyrock.mongock.decorator.operation.executable.find.impl;

import com.github.cloudyrock.mongock.decorator.operation.executable.find.TerminatingDistinctDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public class TerminatingDistinctDecoratorImpl<T> implements TerminatingDistinctDecorator<T> {

  private final ExecutableFindOperation.TerminatingDistinct<T> impl;
  private final MethodInvoker invoker;

  public TerminatingDistinctDecoratorImpl(ExecutableFindOperation.TerminatingDistinct<T> impl, MethodInvoker invoker) {
    this.impl = impl;
    this.invoker = invoker;
  }

  @Override
  public ExecutableFindOperation.TerminatingDistinct<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return invoker;
  }
}
