package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.update.impl;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.update.UpdateWithCollectionDecorator;
import io.changock.driver.api.lock.guard.decorator.DecoratorBase;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class UpdateWithCollectionDecoratorImpl<T> extends DecoratorBase<ExecutableUpdateOperation.UpdateWithCollection<T>> implements UpdateWithCollectionDecorator<T> {

  public UpdateWithCollectionDecoratorImpl(ExecutableUpdateOperation.UpdateWithCollection<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
