package io.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.find.impl;

import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.find.TerminatingDistinctDecorator;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public class TerminatingDistinctDecoratorImpl<T> implements TerminatingDistinctDecorator<T> {

  private final ExecutableFindOperation.TerminatingDistinct<T> impl;
  private final LockGuardInvoker invoker;

  public TerminatingDistinctDecoratorImpl(ExecutableFindOperation.TerminatingDistinct<T> impl, LockGuardInvoker invoker) {
    this.impl = impl;
    this.invoker = invoker;
  }

  @Override
  public ExecutableFindOperation.TerminatingDistinct<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return invoker;
  }
}
