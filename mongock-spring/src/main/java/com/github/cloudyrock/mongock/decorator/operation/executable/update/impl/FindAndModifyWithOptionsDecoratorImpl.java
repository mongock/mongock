package com.github.cloudyrock.mongock.decorator.operation.executable.update.impl;

import com.github.cloudyrock.mongock.decorator.util.DecoratorBase;
import com.github.cloudyrock.mongock.decorator.operation.executable.update.FindAndModifyWithOptionsDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class FindAndModifyWithOptionsDecoratorImpl<T> extends DecoratorBase<ExecutableUpdateOperation.FindAndModifyWithOptions<T>> implements FindAndModifyWithOptionsDecorator<T> {

  public FindAndModifyWithOptionsDecoratorImpl(ExecutableUpdateOperation.FindAndModifyWithOptions<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }

}
