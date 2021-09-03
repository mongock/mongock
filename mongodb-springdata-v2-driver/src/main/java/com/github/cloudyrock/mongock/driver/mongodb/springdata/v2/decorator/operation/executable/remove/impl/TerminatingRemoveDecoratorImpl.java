package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.remove.impl;

import io.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.remove.TerminatingRemoveDecorator;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;

public class TerminatingRemoveDecoratorImpl<T> extends DecoratorBase<ExecutableRemoveOperation.TerminatingRemove<T>> implements TerminatingRemoveDecorator<T> {

  public TerminatingRemoveDecoratorImpl(ExecutableRemoveOperation.TerminatingRemove<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }

}
