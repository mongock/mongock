package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.remove;

import io.changock.driver.api.lock.guard.decorator.Invokable;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.remove.impl.RemoveWithQueryDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;

public interface RemoveWithCollectionDecorator<T> extends Invokable, ExecutableRemoveOperation.RemoveWithCollection<T>, RemoveWithQueryDecorator<T> {

  ExecutableRemoveOperation.RemoveWithCollection<T> getImpl();

  @Override
  default ExecutableRemoveOperation.RemoveWithQuery<T> inCollection(String collection) {
    return new RemoveWithQueryDecoratorImpl<>(getInvoker().invoke(()-> getImpl().inCollection(collection)), getInvoker());
  }
}
