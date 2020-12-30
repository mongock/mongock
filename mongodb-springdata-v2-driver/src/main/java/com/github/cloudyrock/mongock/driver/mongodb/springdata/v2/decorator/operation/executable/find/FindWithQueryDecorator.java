package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.find;

import com.github.cloudyrock.mongock.driver.api.lock.guard.decorator.Invokable;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.find.impl.TerminatingFindDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.find.impl.TerminatingFindNearDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableFindOperation;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;

public interface FindWithQueryDecorator<T> extends Invokable, ExecutableFindOperation.FindWithQuery<T>, TerminatingFindDecorator<T> {

  ExecutableFindOperation.FindWithQuery<T> getImpl();


  @Override
  default ExecutableFindOperation.TerminatingFind<T> matching(Query query) {
    return new TerminatingFindDecoratorImpl<>(getInvoker().invoke(()-> getImpl().matching(query)), getInvoker());
  }

  @Override
  default ExecutableFindOperation.TerminatingFindNear<T> near(NearQuery nearQuery) {
    return new TerminatingFindNearDecoratorImpl<>(getInvoker().invoke(()-> getImpl().near(nearQuery)), getInvoker());
  }
}
