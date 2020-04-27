package com.github.cloudyrock.mongock.decorator.operation.executable.update.impl;

import com.github.cloudyrock.mongock.decorator.util.DecoratorBase;
import com.github.cloudyrock.mongock.decorator.operation.executable.update.UpdateWithQueryDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class UpdateWithQueryDecoratorImpl<T>
    extends DecoratorBase<ExecutableUpdateOperation.UpdateWithQuery<T>>
    implements UpdateWithQueryDecorator<T> {

  public UpdateWithQueryDecoratorImpl(ExecutableUpdateOperation.UpdateWithQuery<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }

}
