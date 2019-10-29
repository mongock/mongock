package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.ListCollectionsIterableDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.mongodb.client.ListCollectionsIterable;
import com.mongodb.client.MongoCursor;

public class ListCollectionsIterableDecoratorImpl<T> implements ListCollectionsIterableDecorator<T> {

  private final ListCollectionsIterable<T> impl;
  private final MethodInvoker checker;

  public ListCollectionsIterableDecoratorImpl(ListCollectionsIterable<T> implementation, MethodInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public ListCollectionsIterable<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return checker;
  }

}
