package io.mongock.driver.mongodb.sync.v4.decorator.impl;

import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.mongock.driver.mongodb.sync.v4.decorator.ChangeStreamIterableDecorator;
import com.mongodb.client.ChangeStreamIterable;

public class ChangeStreamIterableDecoratorImpl<T> implements ChangeStreamIterableDecorator<T> {

  private final ChangeStreamIterable<T> impl;
  private final LockGuardInvoker checker;

  public ChangeStreamIterableDecoratorImpl(ChangeStreamIterable<T> implementation, LockGuardInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public ChangeStreamIterable<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return checker;
  }
}
