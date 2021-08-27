package io.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.find;

import io.mongock.driver.api.lock.guard.decorator.Invokable;
import io.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.find.impl.FindWithProjectionDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public interface FindWithCollectionDecorator<T> extends Invokable, ExecutableFindOperation.FindWithCollection<T>, FindWithQueryDecorator<T> {

  ExecutableFindOperation.FindWithCollection<T> getImpl();

  @Override
  default ExecutableFindOperation.FindWithProjection<T> inCollection(String collection) {
    return new FindWithProjectionDecoratorImpl<>(getInvoker().invoke(()-> getImpl().inCollection(collection)), getInvoker());
  }
}
