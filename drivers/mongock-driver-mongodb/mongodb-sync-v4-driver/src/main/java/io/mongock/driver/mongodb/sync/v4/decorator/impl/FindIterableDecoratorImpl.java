package io.mongock.driver.mongodb.sync.v4.decorator.impl;

import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.mongock.driver.mongodb.sync.v4.decorator.FindIterableDecorator;
import com.mongodb.client.FindIterable;

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
