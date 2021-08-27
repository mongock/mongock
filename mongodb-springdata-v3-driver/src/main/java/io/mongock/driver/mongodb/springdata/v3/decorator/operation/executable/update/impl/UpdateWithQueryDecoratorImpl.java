package io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.update.impl;

import io.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.update.UpdateWithQueryDecorator;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class UpdateWithQueryDecoratorImpl<T>
    extends DecoratorBase<ExecutableUpdateOperation.UpdateWithQuery<T>>
    implements UpdateWithQueryDecorator<T> {

  public UpdateWithQueryDecoratorImpl(ExecutableUpdateOperation.UpdateWithQuery<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }

}
