package com.github.cloudyrock.mongock.decorator.operation.executable.update.impl;

import com.github.cloudyrock.mongock.decorator.util.DecoratorBase;
import com.github.cloudyrock.mongock.decorator.operation.executable.update.TerminatingFindAndReplaceDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class TerminatingFindAndReplaceDecoratorImpl<T> extends DecoratorBase<ExecutableUpdateOperation.TerminatingFindAndReplace<T>> implements TerminatingFindAndReplaceDecorator<T> {

  public TerminatingFindAndReplaceDecoratorImpl(ExecutableUpdateOperation.TerminatingFindAndReplace<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }

}
