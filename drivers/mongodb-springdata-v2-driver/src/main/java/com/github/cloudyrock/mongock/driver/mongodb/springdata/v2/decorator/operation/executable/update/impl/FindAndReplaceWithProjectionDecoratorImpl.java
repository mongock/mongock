package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.update.impl;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.update.FindAndReplaceWithProjectionDecorator;
import com.github.cloudyrock.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class FindAndReplaceWithProjectionDecoratorImpl<T>
    extends DecoratorBase<ExecutableUpdateOperation.FindAndReplaceWithProjection<T>>
    implements FindAndReplaceWithProjectionDecorator<T> {

  public FindAndReplaceWithProjectionDecoratorImpl(ExecutableUpdateOperation.FindAndReplaceWithProjection<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }



}
