package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.insert;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.insert.impl.InsertWithBulkModeDecoratorImpl;
import io.changock.driver.api.lock.guard.decorator.Invokable;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public interface InsertWithCollectionDecorator<T> extends Invokable, ExecutableInsertOperation.InsertWithCollection<T> {

  ExecutableInsertOperation.InsertWithCollection<T> getImpl();

  @Override
  default ExecutableInsertOperation.InsertWithBulkMode<T> inCollection(String collection) {
    return new InsertWithBulkModeDecoratorImpl<>(getInvoker().invoke(()-> getImpl().inCollection(collection)), getInvoker());
  }
}
