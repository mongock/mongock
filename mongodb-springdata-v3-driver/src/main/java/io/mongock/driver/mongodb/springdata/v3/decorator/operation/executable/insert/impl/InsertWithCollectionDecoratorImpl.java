package io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.insert.impl;

import io.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.insert.InsertWithCollectionDecorator;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public class InsertWithCollectionDecoratorImpl<T>
    extends DecoratorBase<ExecutableInsertOperation.InsertWithCollection<T>>
    implements InsertWithCollectionDecorator<T> {
  public InsertWithCollectionDecoratorImpl(ExecutableInsertOperation.InsertWithCollection<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
