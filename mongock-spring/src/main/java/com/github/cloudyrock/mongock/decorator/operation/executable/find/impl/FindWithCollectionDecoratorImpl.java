package com.github.cloudyrock.mongock.decorator.operation.executable.find.impl;

import com.github.cloudyrock.mongock.decorator.operation.executable.find.FindWithCollectionDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public class FindWithCollectionDecoratorImpl<T> implements FindWithCollectionDecorator<T> {

  private final ExecutableFindOperation.FindWithCollection<T> impl;

  private final MethodInvoker invoker;

  public FindWithCollectionDecoratorImpl(ExecutableFindOperation.FindWithCollection<T> impl, MethodInvoker invoker) {
    this.impl = impl;
    this.invoker = invoker;
  }

  @Override
  public ExecutableFindOperation.FindWithCollection<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return invoker;
  }
}
