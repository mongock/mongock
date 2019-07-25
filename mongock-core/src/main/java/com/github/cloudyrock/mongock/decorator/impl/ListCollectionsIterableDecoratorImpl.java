package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.ListCollectionsIterableDecorator;
import com.github.cloudyrock.mongock.decorator.util.LockCheckInvoker;
import com.mongodb.client.ListCollectionsIterable;

public class ListCollectionsIterableDecoratorImpl<T> implements ListCollectionsIterableDecorator<T> {

  private final ListCollectionsIterable<T> impl;
  private final LockCheckInvoker checker;


  public ListCollectionsIterableDecoratorImpl(ListCollectionsIterable<T> implementation, LockCheckInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public ListCollectionsIterable<T> getImpl() {
    return impl;
  }

  @Override
  public LockCheckInvoker getInvoker() {
    return checker;
  }
}
