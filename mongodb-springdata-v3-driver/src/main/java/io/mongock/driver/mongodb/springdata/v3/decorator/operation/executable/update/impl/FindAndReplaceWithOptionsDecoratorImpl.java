package io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.update.impl;

import io.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.update.FindAndReplaceWithOptionsDecorator;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class FindAndReplaceWithOptionsDecoratorImpl<T>
    extends DecoratorBase<ExecutableUpdateOperation.FindAndReplaceWithOptions<T>>
    implements FindAndReplaceWithOptionsDecorator<T> {

  public FindAndReplaceWithOptionsDecoratorImpl(ExecutableUpdateOperation.FindAndReplaceWithOptions<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }

}
