package com.github.cloudyrock.mongock.decorator.operation.executable.update;

import com.github.cloudyrock.mongock.decorator.util.Invokable;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public interface TerminatingFindAndReplaceDecorator<T> extends Invokable, ExecutableUpdateOperation.TerminatingFindAndReplace<T> {

  ExecutableUpdateOperation.TerminatingFindAndReplace<T>  getImpl();

  @Override
  default T findAndReplaceValue() {
    return getInvoker().invoke(()-> getImpl().findAndReplaceValue());
  }
}
