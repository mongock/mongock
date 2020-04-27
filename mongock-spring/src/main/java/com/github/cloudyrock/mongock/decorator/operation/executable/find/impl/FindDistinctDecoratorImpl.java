package com.github.cloudyrock.mongock.decorator.operation.executable.find.impl;

import com.github.cloudyrock.mongock.decorator.operation.executable.find.FindDistinctDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public class FindDistinctDecoratorImpl implements FindDistinctDecorator {

  private final ExecutableFindOperation.FindDistinct impl;

  private final MethodInvoker invoker;

  public FindDistinctDecoratorImpl(ExecutableFindOperation.FindDistinct impl, MethodInvoker invoker) {
    this.impl = impl;
    this.invoker = invoker;
  }

  @Override
  public ExecutableFindOperation.FindDistinct getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return invoker;
  }
}
