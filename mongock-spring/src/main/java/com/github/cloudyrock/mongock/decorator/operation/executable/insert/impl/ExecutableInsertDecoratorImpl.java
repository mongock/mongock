package com.github.cloudyrock.mongock.decorator.operation.executable.insert.impl;

import com.github.cloudyrock.mongock.decorator.operation.executable.insert.ExecutableInsertDecorator;
import com.github.cloudyrock.mongock.decorator.util.DecoratorBase;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public class ExecutableInsertDecoratorImpl<T>
    extends DecoratorBase<ExecutableInsertOperation.ExecutableInsert<T>>
    implements ExecutableInsertDecorator<T> {
  public ExecutableInsertDecoratorImpl(ExecutableInsertOperation.ExecutableInsert<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }
}
