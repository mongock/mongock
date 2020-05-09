package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.find.impl;

import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.find.FindDistinctDecorator;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public class FindDistinctDecoratorImpl implements FindDistinctDecorator {

  private final ExecutableFindOperation.FindDistinct impl;

  private final LockGuardInvoker invoker;

  public FindDistinctDecoratorImpl(ExecutableFindOperation.FindDistinct impl, LockGuardInvoker invoker) {
    this.impl = impl;
    this.invoker = invoker;
  }

  @Override
  public ExecutableFindOperation.FindDistinct getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return invoker;
  }
}
