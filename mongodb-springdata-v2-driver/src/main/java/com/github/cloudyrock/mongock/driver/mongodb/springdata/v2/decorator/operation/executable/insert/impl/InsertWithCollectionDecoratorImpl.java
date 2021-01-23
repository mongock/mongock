package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.insert.impl;

import com.github.cloudyrock.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.insert.InsertWithCollectionDecorator;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public class InsertWithCollectionDecoratorImpl<T>
    extends DecoratorBase<ExecutableInsertOperation.InsertWithCollection<T>>
    implements InsertWithCollectionDecorator<T> {
  public InsertWithCollectionDecoratorImpl(ExecutableInsertOperation.InsertWithCollection<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
