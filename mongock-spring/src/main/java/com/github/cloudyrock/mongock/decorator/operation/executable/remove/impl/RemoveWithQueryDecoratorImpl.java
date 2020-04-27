package com.github.cloudyrock.mongock.decorator.operation.executable.remove.impl;

import com.github.cloudyrock.mongock.decorator.util.DecoratorBase;
import com.github.cloudyrock.mongock.decorator.operation.executable.remove.RemoveWithQueryDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;

public class RemoveWithQueryDecoratorImpl<T> extends DecoratorBase<ExecutableRemoveOperation.RemoveWithQuery<T>> implements RemoveWithQueryDecorator<T> {
  public RemoveWithQueryDecoratorImpl(ExecutableRemoveOperation.RemoveWithQuery<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }
}
