package com.github.cloudyrock.mongock.decorator.operation.executable.remove.impl;

import com.github.cloudyrock.mongock.decorator.util.DecoratorBase;
import com.github.cloudyrock.mongock.decorator.operation.executable.remove.ExecutableRemoveDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;

public class ExecutableRemoveDecoratorImpl<T> extends DecoratorBase<ExecutableRemoveOperation.ExecutableRemove<T>> implements ExecutableRemoveDecorator<T> {
  public ExecutableRemoveDecoratorImpl(ExecutableRemoveOperation.ExecutableRemove<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }
}
