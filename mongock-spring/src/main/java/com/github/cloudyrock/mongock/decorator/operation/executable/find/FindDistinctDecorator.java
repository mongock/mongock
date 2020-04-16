package com.github.cloudyrock.mongock.decorator.operation.executable.find;

import com.github.cloudyrock.mongock.decorator.util.Invokable;
import com.github.cloudyrock.mongock.decorator.operation.executable.find.impl.TerminatingDistinctDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public interface FindDistinctDecorator extends Invokable, ExecutableFindOperation.FindDistinct {

  ExecutableFindOperation.FindDistinct getImpl();


  @Override
  default ExecutableFindOperation.TerminatingDistinct<Object> distinct(String field) {
    return new TerminatingDistinctDecoratorImpl<>(getInvoker().invoke(()-> getImpl().distinct(field)), getInvoker());
  }
}
