package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.util.LockCheckInvoker;
import com.github.cloudyrock.mongock.decorator.MongoCursorDecorator;
import com.mongodb.client.MongoCursor;

public class MongoCursorDecoratorImpl<T> implements MongoCursorDecorator<T> {

  private final MongoCursor<T> impl;
  private final LockCheckInvoker checker;


  public MongoCursorDecoratorImpl(MongoCursor<T> implementation, LockCheckInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public MongoCursor<T> getImpl() {
    return impl;
  }

  @Override
  public LockCheckInvoker getInvoker() {
    return checker;
  }
}
