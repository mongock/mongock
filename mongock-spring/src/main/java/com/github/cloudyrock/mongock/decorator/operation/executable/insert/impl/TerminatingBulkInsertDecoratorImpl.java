package com.github.cloudyrock.mongock.decorator.operation.executable.insert.impl;

import com.github.cloudyrock.mongock.decorator.operation.executable.insert.TerminatingBulkInsertDecorator;
import com.github.cloudyrock.mongock.decorator.util.DecoratorBase;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public class TerminatingBulkInsertDecoratorImpl<T>
    extends DecoratorBase<ExecutableInsertOperation.TerminatingBulkInsert<T>>
    implements TerminatingBulkInsertDecorator<T> {
  public TerminatingBulkInsertDecoratorImpl(ExecutableInsertOperation.TerminatingBulkInsert<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }
}
