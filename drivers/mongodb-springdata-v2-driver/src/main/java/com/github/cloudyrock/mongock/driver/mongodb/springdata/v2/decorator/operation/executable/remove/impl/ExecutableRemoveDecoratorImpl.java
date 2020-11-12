package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.remove.impl;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.remove.ExecutableRemoveDecorator;
import io.changock.driver.api.lock.guard.decorator.DecoratorBase;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;

public class ExecutableRemoveDecoratorImpl<T> extends DecoratorBase<ExecutableRemoveOperation.ExecutableRemove<T>> implements ExecutableRemoveDecorator<T> {
  public ExecutableRemoveDecoratorImpl(ExecutableRemoveOperation.ExecutableRemove<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
