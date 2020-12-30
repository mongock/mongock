package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.remove;

import com.github.cloudyrock.mongock.driver.api.lock.guard.decorator.Invokable;
import com.mongodb.client.result.DeleteResult;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;

import java.util.List;

public interface TerminatingRemoveDecorator<T> extends Invokable, ExecutableRemoveOperation.TerminatingRemove<T> {

  ExecutableRemoveOperation.TerminatingRemove<T> getImpl();

  @Override
  default DeleteResult all() {
    return getInvoker().invoke(()-> getImpl().all());
  }

  @Override
  default DeleteResult one() {
    return getInvoker().invoke(()-> getImpl().one());
  }

  @Override
  default List<T> findAndRemove() {
    return getInvoker().invoke(()-> getImpl().findAndRemove());
  }

}
