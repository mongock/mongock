package com.github.cloudyrock.mongock.decorator.operation.executable.update;


import com.github.cloudyrock.mongock.decorator.util.Invokable;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public interface TerminatingUpdateDecorator<T> extends Invokable, ExecutableUpdateOperation.TerminatingUpdate<T>, TerminatingFindAndModifyDecorator<T>, FindAndModifyWithOptionsDecorator<T> {

  ExecutableUpdateOperation.TerminatingUpdate<T> getImpl();

  @Override
  default UpdateResult all() {
    return getInvoker().invoke(()-> getImpl().all());
  }

  @Override
  default UpdateResult first() {
    return getInvoker().invoke(()-> getImpl().first());
  }

  @Override
  default UpdateResult upsert() {
    return getInvoker().invoke(()-> getImpl().upsert());
  }
}
