package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.update;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.update.impl.FindAndReplaceWithProjectionDecoratorImpl;
import io.changock.driver.api.lock.guard.decorator.Invokable;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;

public interface FindAndReplaceWithOptionsDecorator<T> extends Invokable, ExecutableUpdateOperation.FindAndReplaceWithOptions<T>, TerminatingFindAndReplaceDecorator<T> {

  ExecutableUpdateOperation.FindAndReplaceWithOptions<T> getImpl();

  @Override
  default ExecutableUpdateOperation.FindAndReplaceWithProjection<T> withOptions(FindAndReplaceOptions options) {
    return new FindAndReplaceWithProjectionDecoratorImpl<>(getInvoker().invoke(()-> getImpl().withOptions(options)), getInvoker());
  }
}
