package io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.remove.impl;

import io.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.remove.RemoveWithQueryDecorator;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;

public class RemoveWithQueryDecoratorImpl<T> extends DecoratorBase<ExecutableRemoveOperation.RemoveWithQuery<T>> implements RemoveWithQueryDecorator<T> {
  public RemoveWithQueryDecoratorImpl(ExecutableRemoveOperation.RemoveWithQuery<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
