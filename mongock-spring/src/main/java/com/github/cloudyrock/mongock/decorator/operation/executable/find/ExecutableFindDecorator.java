package com.github.cloudyrock.mongock.decorator.operation.executable.find;

import com.github.cloudyrock.mongock.decorator.util.Invokable;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableFindOperation;


public interface ExecutableFindDecorator<T> extends Invokable, ExecutableFindOperation.ExecutableFind<T>, FindWithCollectionDecorator<T>, FindWithProjectionDecorator<T>, FindDistinctDecorator {


  ExecutableFindOperation.ExecutableFind<T> getImpl();

  MethodInvoker getInvoker();
}
