package com.github.cloudyrock.mongock.decorator.operation.executable.update.impl;

import com.github.cloudyrock.mongock.decorator.util.DecoratorBase;
import com.github.cloudyrock.mongock.decorator.operation.executable.update.ExecutableUpdateDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class ExecutableUpdateDecoratorImpl<T> extends DecoratorBase<ExecutableUpdateOperation.ExecutableUpdate<T>> implements ExecutableUpdateDecorator<T> {

  public ExecutableUpdateDecoratorImpl(ExecutableUpdateOperation.ExecutableUpdate<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }

}
