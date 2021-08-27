package io.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.update;

import io.mongock.driver.api.lock.guard.decorator.Invokable;
import io.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.update.impl.UpdateWithQueryDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public interface UpdateWithCollectionDecorator<T> extends Invokable, ExecutableUpdateOperation.UpdateWithCollection<T> {

  ExecutableUpdateOperation.UpdateWithCollection<T> getImpl();

  @Override
  default ExecutableUpdateOperation.UpdateWithQuery<T> inCollection(String collection) {
    return new UpdateWithQueryDecoratorImpl<>(getInvoker().invoke(()-> getImpl().inCollection(collection)), getInvoker());
  }
}
