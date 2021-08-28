package io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.update;

import io.mongock.driver.api.lock.guard.decorator.Invokable;
import io.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.update.impl.TerminatingFindAndModifyDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;
import org.springframework.data.mongodb.core.FindAndModifyOptions;

public interface FindAndModifyWithOptionsDecorator<T> extends Invokable, ExecutableUpdateOperation.FindAndModifyWithOptions<T> {

  ExecutableUpdateOperation.FindAndModifyWithOptions<T>  getImpl();


  @Override
  default ExecutableUpdateOperation.TerminatingFindAndModify<T> withOptions(FindAndModifyOptions options) {
    return new TerminatingFindAndModifyDecoratorImpl<>(getInvoker().invoke(()-> getImpl().withOptions(options)), getInvoker());
  }
}
