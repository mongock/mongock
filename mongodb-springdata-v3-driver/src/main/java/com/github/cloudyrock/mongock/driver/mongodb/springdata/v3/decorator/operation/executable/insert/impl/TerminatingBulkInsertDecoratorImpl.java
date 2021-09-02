package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.insert.impl;

import io.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.insert.TerminatingBulkInsertDecorator;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public class TerminatingBulkInsertDecoratorImpl<T>
    extends DecoratorBase<ExecutableInsertOperation.TerminatingBulkInsert<T>>
    implements TerminatingBulkInsertDecorator<T> {
  public TerminatingBulkInsertDecoratorImpl(ExecutableInsertOperation.TerminatingBulkInsert<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
