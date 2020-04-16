package com.github.cloudyrock.mongock.decorator.operation.executable.update;

import com.github.cloudyrock.mongock.decorator.util.Invokable;
import com.github.cloudyrock.mongock.decorator.operation.executable.update.impl.FindAndReplaceWithProjectionDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.update.impl.TerminatingUpdateDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;
import org.springframework.data.mongodb.core.query.Update;

public interface UpdateWithUpdateDecorator<T> extends Invokable, ExecutableUpdateOperation.UpdateWithUpdate<T> {

  ExecutableUpdateOperation.UpdateWithUpdate<T> getImpl();


  @Override
  default ExecutableUpdateOperation.TerminatingUpdate<T> apply(Update update) {
    return new TerminatingUpdateDecoratorImpl<>(getInvoker().invoke(() -> getImpl().apply(update)), getInvoker());
  }

  @Override
  default ExecutableUpdateOperation.FindAndReplaceWithProjection<T> replaceWith(T replacement) {
    return new FindAndReplaceWithProjectionDecoratorImpl<>(getInvoker().invoke(() -> getImpl().replaceWith(replacement)), getInvoker());
  }

}
