package io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.update.impl;

import io.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.update.UpdateWithCollectionDecorator;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class UpdateWithCollectionDecoratorImpl<T> extends DecoratorBase<ExecutableUpdateOperation.UpdateWithCollection<T>> implements UpdateWithCollectionDecorator<T> {

  public UpdateWithCollectionDecoratorImpl(ExecutableUpdateOperation.UpdateWithCollection<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
