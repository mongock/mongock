package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.update;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.update.impl.FindAndReplaceWithProjectionDecoratorImpl;
import io.changock.driver.api.lock.guard.decorator.Invokable;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;

import java.util.Optional;

public interface FindAndReplaceWithOptionsDecorator<T> extends Invokable, ExecutableUpdateOperation.FindAndReplaceWithOptions<T>, TerminatingFindAndReplaceDecorator<T> {

  ExecutableUpdateOperation.FindAndReplaceWithOptions<T> getImpl();

  @Override
  default ExecutableUpdateOperation.FindAndReplaceWithProjection<T> withOptions(FindAndReplaceOptions options) {
    return new FindAndReplaceWithProjectionDecoratorImpl<>(getInvoker().invoke(()-> getImpl().withOptions(options)), getInvoker());
  }

  @Override
  default Optional<T> findAndReplace() {
    return getInvoker().invoke(()-> getImpl().findAndReplace());
  }

}
