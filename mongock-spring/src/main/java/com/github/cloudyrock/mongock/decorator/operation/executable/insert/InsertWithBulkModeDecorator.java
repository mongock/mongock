package com.github.cloudyrock.mongock.decorator.operation.executable.insert;

import com.github.cloudyrock.mongock.decorator.operation.executable.insert.impl.TerminatingBulkInsertDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.util.Invokable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public interface InsertWithBulkModeDecorator<T> extends Invokable, ExecutableInsertOperation.InsertWithBulkMode<T>, TerminatingInsertDecorator<T> {

  ExecutableInsertOperation.InsertWithBulkMode<T> getImpl();

  @Override
  default ExecutableInsertOperation.TerminatingBulkInsert<T> withBulkMode(BulkOperations.BulkMode bulkMode) {
    return new TerminatingBulkInsertDecoratorImpl<>(getInvoker().invoke(()-> getImpl().withBulkMode(bulkMode)), getInvoker());
  }
}
