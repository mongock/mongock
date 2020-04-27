package com.github.cloudyrock.mongock.decorator.operation.executable.find;

import static org.springframework.data.mongodb.core.ExecutableFindOperation.TerminatingDistinct;

import com.github.cloudyrock.mongock.decorator.operation.executable.find.impl.TerminatingDistinctDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public interface TerminatingDistinctDecorator<T> extends TerminatingDistinct<T> {

  TerminatingDistinct<T> getImpl();

  MethodInvoker getInvoker();

  @Override
  default List<T> all() {
    return getInvoker().invoke(() -> getImpl().all());
  }

  @Override
  default TerminatingDistinct<T> matching(Query query) {
    TerminatingDistinct<T> result = getInvoker().invoke(() -> getImpl().matching(query));
    return new TerminatingDistinctDecoratorImpl<>(result, getInvoker());
  }

  @Override
  default  <R> TerminatingDistinct<R> as(Class<R> resultType) {
    TerminatingDistinct<R> result = getInvoker().invoke(() -> getImpl().as(resultType));
    return new TerminatingDistinctDecoratorImpl<>(result, getInvoker());
  }
}
