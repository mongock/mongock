package com.github.cloudyrock.mongock.decorator.operation.executable.update;

import com.github.cloudyrock.mongock.decorator.util.Invokable;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public interface TerminatingFindAndModifyDecorator<T> extends Invokable, ExecutableUpdateOperation.TerminatingFindAndModify<T> {

  ExecutableUpdateOperation.TerminatingFindAndModify<T> getImpl();

  @Override
  default T findAndModifyValue() {
    return getInvoker().invoke(()-> getImpl().findAndModifyValue());
  }
}
