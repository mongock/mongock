package com.github.cloudyrock.mongock.decorator.operation.executable.find;

import com.github.cloudyrock.mongock.decorator.util.Invokable;
import com.github.cloudyrock.mongock.decorator.operation.executable.find.impl.FindWithProjectionDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public interface FindWithCollectionDecorator<T> extends Invokable, ExecutableFindOperation.FindWithCollection<T>, FindWithQueryDecorator<T> {

  ExecutableFindOperation.FindWithCollection<T> getImpl();

  @Override
  default ExecutableFindOperation.FindWithProjection<T> inCollection(String collection) {
    return new FindWithProjectionDecoratorImpl<>(getInvoker().invoke(()-> getImpl().inCollection(collection)), getInvoker());
  }
}
