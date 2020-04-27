package com.github.cloudyrock.mongock.decorator.operation.executable.remove.impl;

import com.github.cloudyrock.mongock.decorator.util.DecoratorBase;
import com.github.cloudyrock.mongock.decorator.operation.executable.remove.RemoveWithCollectionDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;

public class RemoveWithCollectionDecoratorImpl<T>
    extends DecoratorBase<ExecutableRemoveOperation.RemoveWithCollection<T>>
  implements RemoveWithCollectionDecorator<T> {

  public RemoveWithCollectionDecoratorImpl(ExecutableRemoveOperation.RemoveWithCollection<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }
}
