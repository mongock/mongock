package com.github.cloudyrock.mongock.decorator.operation.executable.update.impl;

import com.github.cloudyrock.mongock.decorator.util.DecoratorBase;
import com.github.cloudyrock.mongock.decorator.operation.executable.update.TerminatingUpdateDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class TerminatingUpdateDecoratorImpl<T> extends DecoratorBase<ExecutableUpdateOperation.TerminatingUpdate<T>> implements TerminatingUpdateDecorator<T> {
  public TerminatingUpdateDecoratorImpl(ExecutableUpdateOperation.TerminatingUpdate<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }
}
