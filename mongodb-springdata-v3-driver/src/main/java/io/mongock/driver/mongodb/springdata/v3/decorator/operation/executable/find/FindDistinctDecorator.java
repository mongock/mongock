package io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.find;

import io.mongock.driver.api.lock.guard.decorator.Invokable;
import io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.find.impl.TerminatingDistinctDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public interface FindDistinctDecorator extends Invokable, ExecutableFindOperation.FindDistinct {

  ExecutableFindOperation.FindDistinct getImpl();


  @Override
  default ExecutableFindOperation.TerminatingDistinct<Object> distinct(String field) {
    return new TerminatingDistinctDecoratorImpl<>(getInvoker().invoke(()-> getImpl().distinct(field)), getInvoker());
  }
}
