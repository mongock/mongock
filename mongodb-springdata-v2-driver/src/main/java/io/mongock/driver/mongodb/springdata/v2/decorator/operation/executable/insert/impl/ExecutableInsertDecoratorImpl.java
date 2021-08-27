package io.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.insert.impl;

import io.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.insert.ExecutableInsertDecorator;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public class ExecutableInsertDecoratorImpl<T>
    extends DecoratorBase<ExecutableInsertOperation.ExecutableInsert<T>>
    implements ExecutableInsertDecorator<T> {
  public ExecutableInsertDecoratorImpl(ExecutableInsertOperation.ExecutableInsert<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
