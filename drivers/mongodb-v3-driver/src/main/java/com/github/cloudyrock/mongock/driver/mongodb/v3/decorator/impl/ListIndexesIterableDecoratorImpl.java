package com.github.cloudyrock.mongock.driver.mongodb.v3.decorator.impl;

import com.github.cloudyrock.mongock.driver.mongodb.v3.decorator.ListIndexesIterableDecorator;
import com.mongodb.client.ListIndexesIterable;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;

public class ListIndexesIterableDecoratorImpl<T> implements ListIndexesIterableDecorator<T> {

  private final ListIndexesIterable<T> impl;
  private final LockGuardInvoker checker;

  public ListIndexesIterableDecoratorImpl(ListIndexesIterable<T> implementation, LockGuardInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public ListIndexesIterable<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return checker;
  }

}
