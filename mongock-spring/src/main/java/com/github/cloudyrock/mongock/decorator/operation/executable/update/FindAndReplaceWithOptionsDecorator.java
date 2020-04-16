package com.github.cloudyrock.mongock.decorator.operation.executable.update;

import com.github.cloudyrock.mongock.decorator.util.Invokable;
import com.github.cloudyrock.mongock.decorator.operation.executable.update.impl.FindAndReplaceWithProjectionDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;

public interface FindAndReplaceWithOptionsDecorator<T> extends Invokable, ExecutableUpdateOperation.FindAndReplaceWithOptions<T>, TerminatingFindAndReplaceDecorator<T> {

  ExecutableUpdateOperation.FindAndReplaceWithOptions<T> getImpl();

  @Override
  default ExecutableUpdateOperation.FindAndReplaceWithProjection<T> withOptions(FindAndReplaceOptions options) {
    return new FindAndReplaceWithProjectionDecoratorImpl<>(getInvoker().invoke(()-> getImpl().withOptions(options)), getInvoker());
  }
}
