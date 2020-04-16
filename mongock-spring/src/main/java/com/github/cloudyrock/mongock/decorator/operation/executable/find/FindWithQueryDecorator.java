package com.github.cloudyrock.mongock.decorator.operation.executable.find;

import com.github.cloudyrock.mongock.decorator.util.Invokable;
import com.github.cloudyrock.mongock.decorator.operation.executable.find.impl.TerminatingFindDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.operation.executable.find.impl.TerminatingFindNearDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableFindOperation;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;

public interface FindWithQueryDecorator<T> extends Invokable, ExecutableFindOperation.FindWithQuery<T>, TerminatingFindDecorator<T> {

  ExecutableFindOperation.FindWithQuery<T> getImpl();


  @Override
  default ExecutableFindOperation.TerminatingFind<T> matching(Query query) {
    ExecutableFindOperation.TerminatingFind<T> result = getInvoker().invoke(()-> getImpl().matching(query));
    return new TerminatingFindDecoratorImpl<>(result, getInvoker());
  }

  @Override
  default ExecutableFindOperation.TerminatingFindNear<T> near(NearQuery nearQuery) {
    ExecutableFindOperation.TerminatingFindNear<T> result = getInvoker().invoke(()-> getImpl().near(nearQuery));
    return new TerminatingFindNearDecoratorImpl<>(result, getInvoker());
  }
}
