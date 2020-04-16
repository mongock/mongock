package com.github.cloudyrock.mongock.decorator.operation.executable.update;

import com.github.cloudyrock.mongock.decorator.util.Invokable;
import com.github.cloudyrock.mongock.decorator.operation.executable.update.impl.UpdateWithUpdateDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;
import org.springframework.data.mongodb.core.query.Query;

public interface UpdateWithQueryDecorator<T> extends Invokable, ExecutableUpdateOperation.UpdateWithQuery<T>, UpdateWithUpdateDecorator<T> {

  ExecutableUpdateOperation.UpdateWithQuery<T> getImpl();

  @Override
  default ExecutableUpdateOperation.UpdateWithUpdate<T> matching(Query query) {
    return new UpdateWithUpdateDecoratorImpl<>(getInvoker().invoke(()-> getImpl().matching(query)), getInvoker());
  }
}
