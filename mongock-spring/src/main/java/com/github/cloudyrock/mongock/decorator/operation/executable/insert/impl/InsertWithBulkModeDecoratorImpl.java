package com.github.cloudyrock.mongock.decorator.operation.executable.insert.impl;

import com.github.cloudyrock.mongock.decorator.operation.executable.insert.InsertWithBulkModeDecorator;
import com.github.cloudyrock.mongock.decorator.util.DecoratorBase;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public class InsertWithBulkModeDecoratorImpl<T>
    extends DecoratorBase<ExecutableInsertOperation.InsertWithBulkMode<T>>
    implements InsertWithBulkModeDecorator<T> {
  public InsertWithBulkModeDecoratorImpl(ExecutableInsertOperation.InsertWithBulkMode<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }
}
