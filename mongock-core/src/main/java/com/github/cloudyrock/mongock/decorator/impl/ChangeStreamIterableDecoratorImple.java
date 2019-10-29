package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.ChangeStreamIterableDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.mongodb.client.ChangeStreamIterable;

public class ChangeStreamIterableDecoratorImple<T> implements ChangeStreamIterableDecorator<T> {

  private final ChangeStreamIterable<T> impl;
  private final MethodInvoker checker;

  public ChangeStreamIterableDecoratorImple(ChangeStreamIterable<T> implementation, MethodInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public ChangeStreamIterable<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return checker;
  }
}
