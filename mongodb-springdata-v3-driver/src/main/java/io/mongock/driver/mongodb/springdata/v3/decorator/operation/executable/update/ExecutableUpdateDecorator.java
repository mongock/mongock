package io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.update;

import io.mongock.driver.api.lock.guard.decorator.Invokable;
import io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.update.impl.FindAndReplaceWithProjectionDecoratorImpl;
import io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.update.impl.TerminatingUpdateDecoratorImpl;
import io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.update.impl.UpdateWithQueryDecoratorImpl;
import io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.update.impl.UpdateWithUpdateDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;

public interface ExecutableUpdateDecorator<T> extends Invokable, ExecutableUpdateOperation.ExecutableUpdate<T>,
    ExecutableUpdateOperation.UpdateWithCollection<T>, ExecutableUpdateOperation.UpdateWithQuery<T>, ExecutableUpdateOperation.UpdateWithUpdate<T> {

  ExecutableUpdateOperation.ExecutableUpdate<T> getImpl();

  @Override
  default ExecutableUpdateOperation.UpdateWithQuery<T> inCollection(String collection) {
    return new UpdateWithQueryDecoratorImpl<>(getInvoker().invoke(()-> getImpl().inCollection(collection)), getInvoker());
  }

  @Override
  default ExecutableUpdateOperation.UpdateWithUpdate<T> matching(Query query) {
    return new UpdateWithUpdateDecoratorImpl<>(getInvoker().invoke(()-> getImpl().matching(query)), getInvoker());
  }

  @Override
  default ExecutableUpdateOperation.UpdateWithUpdate<T> matching(CriteriaDefinition criteria) {
    return new UpdateWithUpdateDecoratorImpl<>(getInvoker().invoke(()-> getImpl().matching(criteria)), getInvoker());
  }

  @Override
  default ExecutableUpdateOperation.TerminatingUpdate<T> apply(UpdateDefinition update) {
    return new TerminatingUpdateDecoratorImpl<>(getInvoker().invoke(()-> getImpl().apply(update)), getInvoker());
  }

  @Override
  default ExecutableUpdateOperation.FindAndReplaceWithProjection<T> replaceWith(T replacement) {
    return new FindAndReplaceWithProjectionDecoratorImpl<>(getInvoker().invoke(()-> getImpl().replaceWith(replacement)), getInvoker());
  }
}
