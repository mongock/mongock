package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.find.impl;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.find.TerminatingFindDecorator;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public class TerminatingFindDecoratorImpl<T> implements TerminatingFindDecorator<T> {

  private final ExecutableFindOperation.TerminatingFind<T> impl;
  private final LockGuardInvoker invoker;

  public TerminatingFindDecoratorImpl(ExecutableFindOperation.TerminatingFind<T> impl, LockGuardInvoker invoker) {
    this.impl = impl;
    this.invoker = invoker;
  }

  @Override
  public ExecutableFindOperation.TerminatingFind<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return invoker;
  }
}
