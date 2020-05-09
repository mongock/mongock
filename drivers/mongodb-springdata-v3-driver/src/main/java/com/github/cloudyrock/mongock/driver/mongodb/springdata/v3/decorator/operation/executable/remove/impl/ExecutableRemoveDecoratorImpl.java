package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.remove.impl;

import io.changock.driver.api.lock.guard.decorator.DecoratorBase;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.remove.ExecutableRemoveDecorator;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;

public class ExecutableRemoveDecoratorImpl<T> extends DecoratorBase<ExecutableRemoveOperation.ExecutableRemove<T>> implements ExecutableRemoveDecorator<T> {
  public ExecutableRemoveDecoratorImpl(ExecutableRemoveOperation.ExecutableRemove<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
