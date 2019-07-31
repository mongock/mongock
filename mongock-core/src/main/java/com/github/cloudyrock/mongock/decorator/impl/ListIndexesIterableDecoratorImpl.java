package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.ListIndexesIterableDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.mongodb.client.ListIndexesIterable;

public class ListIndexesIterableDecoratorImpl<T> implements ListIndexesIterableDecorator<T> {

  private final ListIndexesIterable<T> impl;
  private final MethodInvoker checker;

  public ListIndexesIterableDecoratorImpl(ListIndexesIterable<T> implementation, MethodInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public ListIndexesIterable<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return checker;
  }
}
