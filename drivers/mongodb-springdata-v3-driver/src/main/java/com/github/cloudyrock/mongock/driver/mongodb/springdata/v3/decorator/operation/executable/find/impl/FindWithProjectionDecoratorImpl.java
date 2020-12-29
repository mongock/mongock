package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.find.impl;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.find.FindWithProjectionDecorator;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
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
