package com.github.cloudyrock.mongock.driver.mongodb.v3.decorator.impl;

import com.github.cloudyrock.mongock.driver.mongodb.v3.decorator.DistinctIterableDecorator;
import com.mongodb.client.DistinctIterable;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;

public class DistinctIterableDecoratorImpl<T> implements DistinctIterableDecorator<T> {

  private final DistinctIterable<T> impl;
  private final LockGuardInvoker checker;

  public DistinctIterableDecoratorImpl(DistinctIterable<T> implementation, LockGuardInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public DistinctIterable<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return checker;
  }
}
