package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.decorator.impl;

import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.decorator.ListCollectionsIterableDecorator;
import com.mongodb.client.ListCollectionsIterable;

public class ListCollectionsIterableDecoratorImpl<T> implements ListCollectionsIterableDecorator<T> {

  private final ListCollectionsIterable<T> impl;
  private final LockGuardInvoker checker;

  public ListCollectionsIterableDecoratorImpl(ListCollectionsIterable<T> implementation, LockGuardInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public ListCollectionsIterable<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return checker;
  }

}
