package com.github.cloudyrock.mongock.driver.mongodb.v3.decorator.impl;

import com.mongodb.client.ListCollectionsIterable;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.v3.decorator.ListCollectionsIterableDecorator;

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
