package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.update.impl;

import io.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.update.UpdateWithUpdateDecorator;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class UpdateWithUpdateDecoratorImpl<T> extends DecoratorBase<ExecutableUpdateOperation.UpdateWithUpdate<T>> implements UpdateWithUpdateDecorator<T> {

  public UpdateWithUpdateDecoratorImpl(ExecutableUpdateOperation.UpdateWithUpdate<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
