package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.AggregateIterableDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.mongodb.client.AggregateIterable;

public class AggregateIterableDecoratorImpl<T> implements AggregateIterableDecorator<T> {

  private final AggregateIterable<T> impl;
  private final MethodInvoker checker;

  public AggregateIterableDecoratorImpl(AggregateIterable<T> implementation, MethodInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public AggregateIterable<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return checker;
  }
}
