package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.impl;

import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.CloseableIteratorDecorator;
import org.springframework.data.util.CloseableIterator;

public class CloseableIteratorDecoratorImpl<T> implements CloseableIteratorDecorator<T> {

  private final LockGuardInvoker invoker;
  private final CloseableIterator<T> impl;

  public CloseableIteratorDecoratorImpl(CloseableIterator<T> implementation, LockGuardInvoker invoker) {
    this.impl = implementation;
    this.invoker = invoker;
  }

  @Override
  public CloseableIterator<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return invoker;
  }


}
