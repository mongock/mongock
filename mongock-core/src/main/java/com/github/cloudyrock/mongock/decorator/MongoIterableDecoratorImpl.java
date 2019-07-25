package com.github.cloudyrock.mongock.decorator;

import com.mongodb.client.MongoIterable;

public class MongoIterableDecoratorImpl<T> implements MongoIterableDecorator<T> {

  private final MongoIterable<T> impl;
  private final LockCheckInvoker checker;

  public MongoIterableDecoratorImpl(MongoIterable<T> implementation, LockCheckInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public MongoIterable<T> getInstance() {
    return impl;
  }

  @Override
  public LockCheckInvoker getCheckInvoker() {
    return checker;
  }
}
