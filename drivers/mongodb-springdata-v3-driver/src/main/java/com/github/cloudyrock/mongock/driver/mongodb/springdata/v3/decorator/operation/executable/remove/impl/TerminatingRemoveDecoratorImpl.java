package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.remove.impl;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.remove.TerminatingRemoveDecorator;
import io.changock.driver.api.lock.guard.decorator.DecoratorBase;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;

public class TerminatingRemoveDecoratorImpl<T> extends DecoratorBase<ExecutableRemoveOperation.TerminatingRemove<T>> implements TerminatingRemoveDecorator<T> {

  public TerminatingRemoveDecoratorImpl(ExecutableRemoveOperation.TerminatingRemove<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }

}
