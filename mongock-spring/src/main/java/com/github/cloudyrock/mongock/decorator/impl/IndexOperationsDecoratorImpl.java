package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.IndexOperationsDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.index.IndexOperations;

public class IndexOperationsDecoratorImpl implements IndexOperationsDecorator {

  private final MethodInvoker invoker;
  private final IndexOperations impl;

  public IndexOperationsDecoratorImpl(IndexOperations implementation, MethodInvoker invoker) {
    this.impl = implementation;
    this.invoker = invoker;
  }

  @Override
  public IndexOperations getImpl() {
    return null;
  }

  @Override
  public MethodInvoker getInvoker() {
    return null;
  }
}
