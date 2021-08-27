package io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.insert.impl;

import io.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.insert.InsertWithBulkModeDecorator;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public class InsertWithBulkModeDecoratorImpl<T>
    extends DecoratorBase<ExecutableInsertOperation.InsertWithBulkMode<T>>
    implements InsertWithBulkModeDecorator<T> {
  public InsertWithBulkModeDecoratorImpl(ExecutableInsertOperation.InsertWithBulkMode<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
