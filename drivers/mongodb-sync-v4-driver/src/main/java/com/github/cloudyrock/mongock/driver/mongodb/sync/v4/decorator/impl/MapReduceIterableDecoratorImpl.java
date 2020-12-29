package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.decorator.impl;

import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.decorator.MapReduceIterableDecorator;
import com.mongodb.client.MapReduceIterable;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;

public class MapReduceIterableDecoratorImpl<T> implements MapReduceIterableDecorator<T> {

  private final MapReduceIterable<T> impl;
  private final LockGuardInvoker checker;

  public MapReduceIterableDecoratorImpl(MapReduceIterable<T> implementation, LockGuardInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public MapReduceIterable<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return checker;
  }
}
