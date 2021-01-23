package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.update.impl;

import com.github.cloudyrock.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.update.TerminatingFindAndReplaceDecorator;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class TerminatingFindAndReplaceDecoratorImpl<T> extends DecoratorBase<ExecutableUpdateOperation.TerminatingFindAndReplace<T>> implements TerminatingFindAndReplaceDecorator<T> {

  public TerminatingFindAndReplaceDecoratorImpl(ExecutableUpdateOperation.TerminatingFindAndReplace<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }

}
