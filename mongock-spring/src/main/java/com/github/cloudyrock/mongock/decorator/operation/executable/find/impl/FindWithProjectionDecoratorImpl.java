package com.github.cloudyrock.mongock.decorator.operation.executable.find.impl;

import com.github.cloudyrock.mongock.decorator.operation.executable.find.FindWithProjectionDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public class FindWithProjectionDecoratorImpl<T> implements FindWithProjectionDecorator<T> {

  private final ExecutableFindOperation.FindWithProjection<T> impl;

  private final MethodInvoker invoker;

  public FindWithProjectionDecoratorImpl(ExecutableFindOperation.FindWithProjection<T> impl, MethodInvoker invoker) {
    this.impl = impl;
    this.invoker = invoker;
  }

  @Override
  public ExecutableFindOperation.FindWithProjection<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return invoker;
  }
}
