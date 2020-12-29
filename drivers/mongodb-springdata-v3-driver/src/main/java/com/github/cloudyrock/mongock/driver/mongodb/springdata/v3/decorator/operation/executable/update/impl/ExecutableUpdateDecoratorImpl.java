package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.update.impl;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.update.ExecutableUpdateDecorator;
import com.github.cloudyrock.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class ExecutableUpdateDecoratorImpl<T> extends DecoratorBase<ExecutableUpdateOperation.ExecutableUpdate<T>> implements ExecutableUpdateDecorator<T> {

  public ExecutableUpdateDecoratorImpl(ExecutableUpdateOperation.ExecutableUpdate<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }

}
