package io.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.find;

import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.find.impl.TerminatingDistinctDecoratorImpl;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

import static org.springframework.data.mongodb.core.ExecutableFindOperation.TerminatingDistinct;

public interface TerminatingDistinctDecorator<T> extends TerminatingDistinct<T> {

  TerminatingDistinct<T> getImpl();

  LockGuardInvoker getInvoker();

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
