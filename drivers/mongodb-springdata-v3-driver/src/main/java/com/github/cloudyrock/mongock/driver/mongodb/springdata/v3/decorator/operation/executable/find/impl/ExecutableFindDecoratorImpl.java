package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.find.impl;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.find.ExecutableFindDecorator;
import io.changock.driver.api.lock.guard.decorator.DecoratorBase;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public class ExecutableFindDecoratorImpl<T> extends DecoratorBase<ExecutableFindOperation.ExecutableFind<T>> implements ExecutableFindDecorator<T> {

  public ExecutableFindDecoratorImpl(ExecutableFindOperation.ExecutableFind<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }

}
