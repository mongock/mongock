package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.insert;

import com.mongodb.bulk.BulkWriteResult;
import io.changock.driver.api.lock.guard.decorator.Invokable;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

import java.util.Collection;

public interface TerminatingBulkInsertDecorator<T> extends Invokable, ExecutableInsertOperation.TerminatingBulkInsert<T> {

  ExecutableInsertOperation.TerminatingBulkInsert<T> getImpl();

  @Override
  default BulkWriteResult bulk(Collection<? extends T> objects) {
    return getInvoker().invoke(()-> getImpl().bulk(objects));
  }
}
