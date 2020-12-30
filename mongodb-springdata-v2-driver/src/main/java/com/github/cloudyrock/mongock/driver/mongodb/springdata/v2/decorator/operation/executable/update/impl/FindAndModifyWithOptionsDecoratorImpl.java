package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.update.impl;

import com.github.cloudyrock.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.update.FindAndModifyWithOptionsDecorator;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class FindAndModifyWithOptionsDecoratorImpl<T> extends DecoratorBase<ExecutableUpdateOperation.FindAndModifyWithOptions<T>> implements FindAndModifyWithOptionsDecorator<T> {

  public FindAndModifyWithOptionsDecoratorImpl(ExecutableUpdateOperation.FindAndModifyWithOptions<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }

}
