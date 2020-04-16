package com.github.cloudyrock.mongock.decorator.operation.executable.update;

import com.github.cloudyrock.mongock.decorator.util.Invokable;
import com.github.cloudyrock.mongock.decorator.operation.executable.update.impl.FindAndReplaceWithOptionsDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public interface FindAndReplaceWithProjectionDecorator<T>  extends Invokable, ExecutableUpdateOperation.FindAndReplaceWithProjection<T>, FindAndReplaceWithOptionsDecorator<T> {

  ExecutableUpdateOperation.FindAndReplaceWithProjection<T> getImpl();

  @Override
  default  <R> ExecutableUpdateOperation.FindAndReplaceWithOptions<R> as(Class<R> resultType) {
    return new FindAndReplaceWithOptionsDecoratorImpl<>(getInvoker().invoke(()-> getImpl().as(resultType)), getInvoker());
  }
}
