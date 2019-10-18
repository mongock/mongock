package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.MongoChangeStreamCursorDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.mongodb.client.MongoChangeStreamCursor;

public class MongoChangeStreamCursorDecoratorImpl<T> implements MongoChangeStreamCursorDecorator<T> {

  private final MongoChangeStreamCursor<T> impl;
  private final MethodInvoker checker;

  public MongoChangeStreamCursorDecoratorImpl(MongoChangeStreamCursor<T> implementation, MethodInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public MongoChangeStreamCursor<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return checker;
  }
}
