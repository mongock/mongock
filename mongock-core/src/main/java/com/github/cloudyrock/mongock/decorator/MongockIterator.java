package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;

import java.util.Iterator;

public interface MongockIterator<T> extends Iterator<T> {

  Iterator<T> getImpl();

  MethodInvoker getInvoker();

  @Override
  default boolean hasNext() {
    return getInvoker().invoke(() -> getImpl().hasNext());
  }

  @Override
  default T next() {
    return getInvoker().invoke(()-> getImpl().next());
  }
}
