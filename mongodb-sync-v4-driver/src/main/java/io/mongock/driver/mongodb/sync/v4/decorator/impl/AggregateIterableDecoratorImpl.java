package io.mongock.driver.mongodb.sync.v4.decorator.impl;

import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.mongock.driver.mongodb.sync.v4.decorator.AggregateIterableDecorator;
import com.mongodb.client.AggregateIterable;

public class AggregateIterableDecoratorImpl<T> implements AggregateIterableDecorator<T> {

  private final AggregateIterable<T> impl;
  private final LockGuardInvoker checker;

  public AggregateIterableDecoratorImpl(AggregateIterable<T> implementation, LockGuardInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public AggregateIterable<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return checker;
  }

}
