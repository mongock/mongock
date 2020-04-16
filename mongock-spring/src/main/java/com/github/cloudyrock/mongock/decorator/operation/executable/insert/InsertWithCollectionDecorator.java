package com.github.cloudyrock.mongock.decorator.operation.executable.insert;

import com.github.cloudyrock.mongock.decorator.operation.executable.insert.impl.InsertWithBulkModeDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.util.Invokable;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public interface InsertWithCollectionDecorator<T> extends Invokable, ExecutableInsertOperation.InsertWithCollection<T> {

  ExecutableInsertOperation.InsertWithCollection<T> getImpl();

  @Override
  default ExecutableInsertOperation.InsertWithBulkMode<T> inCollection(String collection) {
    return new InsertWithBulkModeDecoratorImpl<>(getInvoker().invoke(()-> getImpl().inCollection(collection)), getInvoker());
  }
}
