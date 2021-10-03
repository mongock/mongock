package io.mongock.driver.mongodb.v3.decorator.impl;

import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.mongock.driver.mongodb.v3.decorator.MapReduceIterableDecorator;
import com.mongodb.client.MapReduceIterable;

public class MapReduceIterableDecoratorImpl<T> implements MapReduceIterableDecorator<T> {

  private final MapReduceIterable<T> impl;
  private final LockGuardInvoker checker;

  public MapReduceIterableDecoratorImpl(MapReduceIterable<T> implementation, LockGuardInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public MapReduceIterable<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return checker;
  }
}
