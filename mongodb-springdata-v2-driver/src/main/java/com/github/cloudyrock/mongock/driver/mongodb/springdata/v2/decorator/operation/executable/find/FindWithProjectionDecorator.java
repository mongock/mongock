package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.find;

import com.github.cloudyrock.mongock.driver.api.lock.guard.decorator.Invokable;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.find.impl.FindWithQueryDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public interface FindWithProjectionDecorator<T> extends Invokable, ExecutableFindOperation.FindWithProjection<T>, FindWithQueryDecorator<T>, FindDistinctDecorator {

  ExecutableFindOperation.FindWithProjection<T> getImpl();

  @Override
  default  <R> ExecutableFindOperation.FindWithQuery<R> as(Class<R> resultType) {
    return new FindWithQueryDecoratorImpl<>(getInvoker().invoke(() -> getImpl().as(resultType)), getInvoker());

  }
}
