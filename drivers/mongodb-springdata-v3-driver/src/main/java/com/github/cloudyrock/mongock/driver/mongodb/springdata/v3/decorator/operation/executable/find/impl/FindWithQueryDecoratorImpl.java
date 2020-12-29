package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.find.impl;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.find.FindWithQueryDecorator;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public class FindWithQueryDecoratorImpl<T> implements FindWithQueryDecorator<T> {

  private final ExecutableFindOperation.FindWithQuery<T> impl;
  private final LockGuardInvoker invoker;

  public FindWithQueryDecoratorImpl(ExecutableFindOperation.FindWithQuery<T> impl, LockGuardInvoker invoker) {
    this.impl = impl;
    this.invoker = invoker;
  }

  @Override
  public ExecutableFindOperation.FindWithQuery<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return invoker;
  }
}
