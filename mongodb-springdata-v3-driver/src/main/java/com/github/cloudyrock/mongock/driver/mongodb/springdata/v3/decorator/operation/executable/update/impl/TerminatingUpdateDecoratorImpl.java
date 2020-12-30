package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.update.impl;

import com.github.cloudyrock.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.update.TerminatingUpdateDecorator;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class TerminatingUpdateDecoratorImpl<T> extends DecoratorBase<ExecutableUpdateOperation.TerminatingUpdate<T>> implements TerminatingUpdateDecorator<T> {
  public TerminatingUpdateDecoratorImpl(ExecutableUpdateOperation.TerminatingUpdate<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
