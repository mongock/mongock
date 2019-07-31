package com.github.cloudyrock.mongock.decorator.util;

public interface MongockDecoratorBase<CLASS> {
  CLASS getImpl();

  MethodInvoker getInvoker();
}
