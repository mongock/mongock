package io.mongock.driver.mongodb.springdata.v2.decorator;

import io.mongock.driver.mongodb.v3.decorator.MongockIterator;
import org.springframework.data.util.CloseableIterator;

public interface CloseableIteratorDecorator<T> extends CloseableIterator<T>, MongockIterator<T> {

  CloseableIterator<T> getImpl();

  @Override
  default void close() {
    getInvoker().invoke(() -> getImpl().close());
  }
}
