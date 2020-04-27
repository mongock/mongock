package com.github.cloudyrock.mongock.decorator.util;

public abstract class DecoratorBase<T> implements Invokable {

  private final MethodInvoker invoker;
  private final T impl;

  public DecoratorBase(T impl, MethodInvoker invoker) {
    this.invoker = invoker;
    this.impl = impl;
  }

  public MethodInvoker getInvoker() {
    return invoker;
  }

  public T getImpl() {
    return impl;
  }
}
