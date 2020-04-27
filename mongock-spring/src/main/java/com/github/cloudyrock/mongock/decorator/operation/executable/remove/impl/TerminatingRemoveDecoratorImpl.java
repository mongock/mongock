package com.github.cloudyrock.mongock.decorator.operation.executable.remove.impl;

import com.github.cloudyrock.mongock.decorator.util.DecoratorBase;
import com.github.cloudyrock.mongock.decorator.operation.executable.remove.TerminatingRemoveDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;

public class TerminatingRemoveDecoratorImpl<T> extends DecoratorBase<ExecutableRemoveOperation.TerminatingRemove<T>> implements TerminatingRemoveDecorator<T> {

  public TerminatingRemoveDecoratorImpl(ExecutableRemoveOperation.TerminatingRemove<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }

}
