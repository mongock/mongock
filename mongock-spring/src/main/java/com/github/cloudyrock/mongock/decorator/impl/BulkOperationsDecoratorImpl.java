package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.BulkOperationsDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.BulkOperations;

public class BulkOperationsDecoratorImpl implements BulkOperationsDecorator {

  private final MethodInvoker invoker;
  private final BulkOperations impl;

  public BulkOperationsDecoratorImpl(BulkOperations implementation, MethodInvoker invoker) {
    this.impl = implementation;
    this.invoker = invoker;
  }

  @Override
  public BulkOperations getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return invoker;
  }
}
