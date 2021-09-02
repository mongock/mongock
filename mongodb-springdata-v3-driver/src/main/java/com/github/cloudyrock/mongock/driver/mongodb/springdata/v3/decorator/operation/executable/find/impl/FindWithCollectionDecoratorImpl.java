package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.find.impl;

import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.find.FindWithCollectionDecorator;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public class FindWithCollectionDecoratorImpl<T> implements FindWithCollectionDecorator<T> {

  private final ExecutableFindOperation.FindWithCollection<T> impl;

  private final LockGuardInvoker invoker;

  public FindWithCollectionDecoratorImpl(ExecutableFindOperation.FindWithCollection<T> impl, LockGuardInvoker invoker) {
    this.impl = impl;
    this.invoker = invoker;
  }

  @Override
  public ExecutableFindOperation.FindWithCollection<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return invoker;
  }
}
