package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.AggregateIterableDecorator;
import com.github.cloudyrock.mongock.decorator.util.LockCheckInvoker;
import com.mongodb.client.AggregateIterable;

public class AggregateIterableDecoratorImpl<T> implements AggregateIterableDecorator<T> {

  private final AggregateIterable<T> impl;
  private final LockCheckInvoker checker;


  public AggregateIterableDecoratorImpl(AggregateIterable<T> implementation, LockCheckInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public AggregateIterable<T> getImpl() {
    return impl;
  }

  @Override
  public LockCheckInvoker getInvoker() {
    return checker;
  }
}
