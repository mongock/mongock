package com.github.cloudyrock.mongock.decorator.operation.executable.insert.impl;

import com.github.cloudyrock.mongock.decorator.operation.executable.insert.TerminatingInsertDecorator;
import com.github.cloudyrock.mongock.decorator.util.DecoratorBase;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public class TerminatingInsertDecoratorImpl<T>
    extends DecoratorBase<ExecutableInsertOperation.TerminatingInsert<T>>
    implements TerminatingInsertDecorator<T> {
  public TerminatingInsertDecoratorImpl(ExecutableInsertOperation.TerminatingInsert<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }
}
