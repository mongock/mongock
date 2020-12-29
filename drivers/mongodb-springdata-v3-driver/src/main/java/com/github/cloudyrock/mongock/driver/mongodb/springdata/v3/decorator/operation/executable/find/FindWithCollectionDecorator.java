package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.find;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.find.impl.FindWithProjectionDecoratorImpl;
import com.github.cloudyrock.mongock.driver.api.lock.guard.decorator.Invokable;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public interface FindWithCollectionDecorator<T> extends Invokable, ExecutableFindOperation.FindWithCollection<T>, FindWithQueryDecorator<T> {

  ExecutableFindOperation.FindWithCollection<T> getImpl();

  @Override
  default ExecutableFindOperation.FindWithProjection<T> inCollection(String collection) {
    return new FindWithProjectionDecoratorImpl<>(getInvoker().invoke(()-> getImpl().inCollection(collection)), getInvoker());
  }
}
