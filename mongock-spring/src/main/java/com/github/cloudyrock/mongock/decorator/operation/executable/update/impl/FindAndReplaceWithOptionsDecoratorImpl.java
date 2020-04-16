package com.github.cloudyrock.mongock.decorator.operation.executable.update.impl;

import com.github.cloudyrock.mongock.decorator.util.DecoratorBase;
import com.github.cloudyrock.mongock.decorator.operation.executable.update.FindAndReplaceWithOptionsDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class FindAndReplaceWithOptionsDecoratorImpl<T>
    extends DecoratorBase<ExecutableUpdateOperation.FindAndReplaceWithOptions<T>>
    implements FindAndReplaceWithOptionsDecorator<T> {

  public FindAndReplaceWithOptionsDecoratorImpl(ExecutableUpdateOperation.FindAndReplaceWithOptions<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }

}
