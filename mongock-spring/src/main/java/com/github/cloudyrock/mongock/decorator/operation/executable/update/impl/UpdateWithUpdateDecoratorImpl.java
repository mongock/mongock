package com.github.cloudyrock.mongock.decorator.operation.executable.update.impl;

import com.github.cloudyrock.mongock.decorator.util.DecoratorBase;
import com.github.cloudyrock.mongock.decorator.operation.executable.update.UpdateWithUpdateDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class UpdateWithUpdateDecoratorImpl<T> extends DecoratorBase<ExecutableUpdateOperation.UpdateWithUpdate<T>> implements UpdateWithUpdateDecorator<T> {

  public UpdateWithUpdateDecoratorImpl(ExecutableUpdateOperation.UpdateWithUpdate<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }
}
