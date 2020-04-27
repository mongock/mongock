package com.github.cloudyrock.mongock.decorator.operation.executable.remove;

import com.github.cloudyrock.mongock.decorator.util.Invokable;
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
    return getInvoker().invoke(()-> getImpl().all());
  }

  @Override
  default List<T> findAndRemove() {
    return getInvoker().invoke(()-> getImpl().findAndRemove());
  }

}
