package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator;

import io.mongock.driver.mongodb.sync.v4.decorator.MongockIterator;
import org.springframework.data.util.CloseableIterator;

public interface CloseableIteratorDecorator<T> extends CloseableIterator<T>, MongockIterator<T> {

  CloseableIterator<T> getImpl();

  @Override
  default void close() {
    getInvoker().invoke(() -> getImpl().close());
  }
}
