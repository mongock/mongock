package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.update.impl;

import io.changock.driver.api.lock.guard.decorator.DecoratorBase;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.update.ExecutableUpdateDecorator;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class ExecutableUpdateDecoratorImpl<T> extends DecoratorBase<ExecutableUpdateOperation.ExecutableUpdate<T>> implements ExecutableUpdateDecorator<T> {

  public ExecutableUpdateDecoratorImpl(ExecutableUpdateOperation.ExecutableUpdate<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }

}
