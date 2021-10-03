package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.update.impl;

import io.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.update.TerminatingFindAndModifyDecorator;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class TerminatingFindAndModifyDecoratorImpl<T> extends DecoratorBase<ExecutableUpdateOperation.TerminatingFindAndModify<T>> implements TerminatingFindAndModifyDecorator<T> {

  public TerminatingFindAndModifyDecoratorImpl(ExecutableUpdateOperation.TerminatingFindAndModify<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }

}
