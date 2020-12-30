package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.find;

import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface TerminatingFindDecorator<T> extends ExecutableFindOperation.TerminatingFind<T> {

  ExecutableFindOperation.TerminatingFind<T> getImpl();

  LockGuardInvoker getInvoker();

  @Override
  default Optional<T> one() {
    return getInvoker().invoke(()-> getImpl().one());
  }

  @Override
  default T oneValue() {
    return getInvoker().invoke(()-> getImpl().oneValue());
  }

  @Override
  default Optional<T> first() {
    return getInvoker().invoke(()-> getImpl().first());
  }

  @Override
  default T firstValue() {
    return getInvoker().invoke(()-> getImpl().firstValue());
  }

  @Override
  default List<T> all() {
    return getInvoker().invoke(()-> getImpl().all());
  }

  @Override
  default Stream<T> stream() {
    return getInvoker().invoke(()-> getImpl().stream());
  }

  @Override
  default long count() {
    return getInvoker().invoke(()-> getImpl().count());
  }

  @Override
  default boolean exists() {
    return getInvoker().invoke(()-> getImpl().exists());
  }
}
