package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.update;


import com.mongodb.client.result.UpdateResult;
import com.github.cloudyrock.mongock.driver.api.lock.guard.decorator.Invokable;
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
