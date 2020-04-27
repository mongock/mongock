package com.github.cloudyrock.mongock.decorator.operation.executable.find;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

import java.util.List;
import java.util.stream.Stream;

public interface TerminatingFindDecorator<T> extends ExecutableFindOperation.TerminatingFind<T> {

  ExecutableFindOperation.TerminatingFind<T> getImpl();

  MethodInvoker getInvoker();

  @Override
  default T oneValue() {
    return getInvoker().invoke(()-> getImpl().oneValue());
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
