package io.mongock.driver.mongodb.sync.v4.decorator.impl;

import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.mongock.driver.mongodb.sync.v4.decorator.MongoIterableDecorator;
import com.mongodb.client.MongoIterable;

public class MongoIterableDecoratorImpl<T> implements MongoIterableDecorator<T> {

  private final MongoIterable<T> impl;
  private final LockGuardInvoker checker;

  public MongoIterableDecoratorImpl(MongoIterable<T> implementation, LockGuardInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public MongoIterable<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return checker;
  }
}
