package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.find.impl;

import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.find.TerminatingFindNearDecorator;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public class TerminatingFindNearDecoratorImpl<T> implements TerminatingFindNearDecorator<T> {

  private final ExecutableFindOperation.TerminatingFindNear<T> impl;

  private final LockGuardInvoker invoker;

  public TerminatingFindNearDecoratorImpl(ExecutableFindOperation.TerminatingFindNear<T> impl, LockGuardInvoker invoker) {
    this.impl = impl;
    this.invoker = invoker;
  }

  @Override
  public ExecutableFindOperation.TerminatingFindNear<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return invoker;
  }
}
