package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.decorator.impl;

import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.decorator.FindIterableDecorator;
import com.mongodb.ExplainVerbosity;
import com.mongodb.client.FindIterable;
import org.bson.Document;

public class FindIterableDecoratorImpl<T> implements FindIterableDecorator<T> {

  private final FindIterable<T> impl;
  private final LockGuardInvoker checker;

  public FindIterableDecoratorImpl(FindIterable<T> implementation, LockGuardInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public FindIterable<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return checker;
  }

}
