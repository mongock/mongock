package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.ScriptOperationsDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ScriptOperations;

public class ScriptOperationsDecoratorImpl implements ScriptOperationsDecorator {

  private final MethodInvoker invoker;
  private final ScriptOperations impl;

  public ScriptOperationsDecoratorImpl(ScriptOperations implementation, MethodInvoker invoker) {
    this.impl = implementation;
    this.invoker = invoker;
  }

  @Override
  public ScriptOperations getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return invoker;
  }


}
