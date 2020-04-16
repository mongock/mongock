package com.github.cloudyrock.mongock.decorator.operation.executable.find.impl;

import com.github.cloudyrock.mongock.decorator.operation.executable.find.TerminatingFindNearDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public class TerminatingFindNearDecoratorImpl<T> implements TerminatingFindNearDecorator<T> {

  private final ExecutableFindOperation.TerminatingFindNear<T> impl;

  private final MethodInvoker invoker;

  public TerminatingFindNearDecoratorImpl(ExecutableFindOperation.TerminatingFindNear<T> impl, MethodInvoker invoker) {
    this.impl = impl;
    this.invoker = invoker;
  }

  @Override
  public ExecutableFindOperation.TerminatingFindNear<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return invoker;
  }
}
