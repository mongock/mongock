package io.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.find.impl;

import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.find.FindWithProjectionDecorator;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public class FindWithProjectionDecoratorImpl<T> implements FindWithProjectionDecorator<T> {

  private final ExecutableFindOperation.FindWithProjection<T> impl;

  private final LockGuardInvoker invoker;

  public FindWithProjectionDecoratorImpl(ExecutableFindOperation.FindWithProjection<T> impl, LockGuardInvoker invoker) {
    this.impl = impl;
    this.invoker = invoker;
  }

  @Override
  public ExecutableFindOperation.FindWithProjection<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return invoker;
  }
}
