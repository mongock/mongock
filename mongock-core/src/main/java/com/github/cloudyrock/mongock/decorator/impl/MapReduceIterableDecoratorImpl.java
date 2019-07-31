package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.github.cloudyrock.mongock.decorator.MapReduceIterableDecorator;
import com.mongodb.client.MapReduceIterable;

public class MapReduceIterableDecoratorImpl<T> implements MapReduceIterableDecorator<T> {

  private final MapReduceIterable<T> impl;
  private final MethodInvoker checker;

  public MapReduceIterableDecoratorImpl(MapReduceIterable<T> implementation, MethodInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public MapReduceIterable<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return checker;
  }
}
