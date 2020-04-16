package com.github.cloudyrock.mongock.decorator.operation.executable.insert;

import com.github.cloudyrock.mongock.decorator.util.Invokable;
import com.mongodb.bulk.BulkWriteResult;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

import java.util.Collection;

public interface TerminatingBulkInsertDecorator<T> extends Invokable, ExecutableInsertOperation.TerminatingBulkInsert<T> {

  ExecutableInsertOperation.TerminatingBulkInsert<T> getImpl();

  @Override
  default BulkWriteResult bulk(Collection<? extends T> objects) {
    return getInvoker().invoke(()-> getImpl().bulk(objects));
  }
}
