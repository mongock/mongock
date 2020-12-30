package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.remove;

import com.github.cloudyrock.mongock.driver.api.lock.guard.decorator.Invokable;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.remove.impl.TerminatingRemoveDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;

public interface RemoveWithQueryDecorator<T> extends Invokable, ExecutableRemoveOperation.RemoveWithQuery<T>, TerminatingRemoveDecorator<T> {

  ExecutableRemoveOperation.RemoveWithQuery<T> getImpl();

  @Override
  default ExecutableRemoveOperation.TerminatingRemove<T> matching(Query query) {
    return new TerminatingRemoveDecoratorImpl<>(getInvoker().invoke(() -> getImpl().matching(query)), getInvoker());
  }

  @Override
  default ExecutableRemoveOperation.TerminatingRemove<T> matching(CriteriaDefinition criteria) {
    return new TerminatingRemoveDecoratorImpl<>(getInvoker().invoke(() -> getImpl().matching(criteria)), getInvoker());
  }

}
