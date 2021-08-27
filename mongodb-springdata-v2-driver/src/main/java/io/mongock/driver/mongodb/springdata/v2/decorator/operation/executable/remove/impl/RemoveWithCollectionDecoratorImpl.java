package io.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.remove.impl;

import io.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.remove.RemoveWithCollectionDecorator;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;

public class RemoveWithCollectionDecoratorImpl<T>
    extends DecoratorBase<ExecutableRemoveOperation.RemoveWithCollection<T>>
  implements RemoveWithCollectionDecorator<T> {

  public RemoveWithCollectionDecoratorImpl(ExecutableRemoveOperation.RemoveWithCollection<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
