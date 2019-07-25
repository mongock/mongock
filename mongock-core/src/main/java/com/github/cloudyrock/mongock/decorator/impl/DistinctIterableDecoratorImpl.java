package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.DistinctIterableDecorator;
import com.github.cloudyrock.mongock.decorator.util.LockCheckInvoker;
import com.mongodb.client.DistinctIterable;

public class DistinctIterableDecoratorImpl<T> implements DistinctIterableDecorator<T> {

  private final DistinctIterable<T> impl;
  private final LockCheckInvoker checker;


  public DistinctIterableDecoratorImpl(DistinctIterable<T> implementation, LockCheckInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public DistinctIterable<T> getImpl() {
    return impl;
  }

  @Override
  public LockCheckInvoker getInvoker() {
    return checker;
  }
}
