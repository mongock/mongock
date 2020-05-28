package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.insert;

import io.changock.driver.api.lock.guard.decorator.Invokable;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.operation.executable.insert.impl.TerminatingBulkInsertDecoratorImpl;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public interface InsertWithBulkModeDecorator<T> extends Invokable, ExecutableInsertOperation.InsertWithBulkMode<T>, TerminatingInsertDecorator<T> {

  ExecutableInsertOperation.InsertWithBulkMode<T> getImpl();

  @Override
  default ExecutableInsertOperation.TerminatingBulkInsert<T> withBulkMode(BulkOperations.BulkMode bulkMode) {
    return new TerminatingBulkInsertDecoratorImpl<>(getInvoker().invoke(()-> getImpl().withBulkMode(bulkMode)), getInvoker());
  }
}
