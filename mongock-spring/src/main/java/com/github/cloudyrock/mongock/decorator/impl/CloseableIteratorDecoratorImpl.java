package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.CloseableIteratorDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.util.CloseableIterator;

public class CloseableIteratorDecoratorImpl<T> implements CloseableIteratorDecorator<T> {

  private final MethodInvoker invoker;
  private final CloseableIterator<T> impl;

  public CloseableIteratorDecoratorImpl(CloseableIterator<T> implementation, MethodInvoker invoker) {
    this.impl = implementation;
    this.invoker = invoker;
  }

  @Override
  public CloseableIterator<T> getImpl() {
    return null;
  }

  @Override
  public MethodInvoker getInvoker() {
    return null;
  }
}
