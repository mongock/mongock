package io.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.find;

import io.mongock.driver.api.lock.guard.decorator.Invokable;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableFindOperation;


public interface ExecutableFindDecorator<T> extends Invokable, ExecutableFindOperation.ExecutableFind<T>, FindWithCollectionDecorator<T>, FindWithProjectionDecorator<T>, FindDistinctDecorator {

  ExecutableFindOperation.ExecutableFind<T> getImpl();

  LockGuardInvoker getInvoker();


}
