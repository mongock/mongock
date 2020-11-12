package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.decorator.impl;

import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.decorator.MongoChangeStreamCursorDecorator;
import com.mongodb.client.MongoChangeStreamCursor;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;

public class MongoChangeStreamCursorDecoratorImpl<T> implements MongoChangeStreamCursorDecorator<T> {

  private final MongoChangeStreamCursor<T> impl;
  private final LockGuardInvoker checker;

  public MongoChangeStreamCursorDecoratorImpl(MongoChangeStreamCursor<T> implementation, LockGuardInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public MongoChangeStreamCursor<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return checker;
  }
}
