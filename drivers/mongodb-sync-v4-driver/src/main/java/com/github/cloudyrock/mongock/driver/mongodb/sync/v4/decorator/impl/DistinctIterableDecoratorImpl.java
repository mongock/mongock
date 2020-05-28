package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.decorator.impl;

import com.mongodb.client.DistinctIterable;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.decorator.DistinctIterableDecorator;

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
