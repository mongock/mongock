package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.github.cloudyrock.mongock.decorator.MongoCursorDecorator;
import com.mongodb.client.MongoCursor;

public class MongoCursorDecoratorImpl<T> implements MongoCursorDecorator<T> {

  private final MongoCursor<T> impl;
  private final MethodInvoker checker;

  public MongoCursorDecoratorImpl(MongoCursor<T> implementation, MethodInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public MongoCursor<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return checker;
  }
}
