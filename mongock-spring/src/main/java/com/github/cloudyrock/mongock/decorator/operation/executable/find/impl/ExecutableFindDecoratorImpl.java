package com.github.cloudyrock.mongock.decorator.operation.executable.find.impl;

import com.github.cloudyrock.mongock.decorator.util.DecoratorBase;
import com.github.cloudyrock.mongock.decorator.operation.executable.find.ExecutableFindDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public class ExecutableFindDecoratorImpl<T> extends DecoratorBase<ExecutableFindOperation.ExecutableFind<T>> implements ExecutableFindDecorator<T> {

  public ExecutableFindDecoratorImpl(ExecutableFindOperation.ExecutableFind<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }

}
