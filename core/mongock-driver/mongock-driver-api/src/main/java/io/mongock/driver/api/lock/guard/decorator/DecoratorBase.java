package io.mongock.driver.api.lock.guard.decorator;

import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;

public abstract class DecoratorBase<T> implements Invokable {

  private final LockGuardInvoker invoker;
  private final T impl;

  public DecoratorBase(T impl, LockGuardInvoker invoker) {
    this.invoker = invoker;
    this.impl = impl;
  }

  public LockGuardInvoker getInvoker() {
    return invoker;
  }

  public T getImpl() {
    return impl;
  }
}
