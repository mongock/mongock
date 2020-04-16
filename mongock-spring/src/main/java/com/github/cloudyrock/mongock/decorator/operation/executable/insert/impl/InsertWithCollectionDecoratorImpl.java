package com.github.cloudyrock.mongock.decorator.operation.executable.insert.impl;

import com.github.cloudyrock.mongock.decorator.operation.executable.insert.InsertWithCollectionDecorator;
import com.github.cloudyrock.mongock.decorator.util.DecoratorBase;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public class InsertWithCollectionDecoratorImpl<T>
    extends DecoratorBase<ExecutableInsertOperation.InsertWithCollection<T>>
    implements InsertWithCollectionDecorator<T> {
  public InsertWithCollectionDecoratorImpl(ExecutableInsertOperation.InsertWithCollection<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }
}
