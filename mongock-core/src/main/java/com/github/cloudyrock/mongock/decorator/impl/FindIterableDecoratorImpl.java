package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.FindIterableDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.mongodb.client.FindIterable;

public class FindIterableDecoratorImpl<T> implements FindIterableDecorator<T> {

  private final FindIterable<T> impl;
  private final MethodInvoker checker;

  public FindIterableDecoratorImpl(FindIterable<T> implementation, MethodInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public FindIterable<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return checker;
  }
}
