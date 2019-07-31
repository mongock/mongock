package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.MongoCursor;

public interface MongoCursorDecorator<T> extends MongoCursor<T> {

  MongoCursor<T> getImpl();

  MethodInvoker getInvoker();

  @Override
  default void close() {
    getInvoker().invoke(() -> getImpl().close());
  }

  @Override
  default boolean hasNext() {
    return getInvoker().invoke(() -> getImpl().hasNext());
  }

  @Override
  default T next() {
    return getInvoker().invoke(() -> getImpl().next());
  }

  @Override
  default T tryNext() {
    return getInvoker().invoke(() -> getImpl().tryNext());
  }

  @Override
  default ServerCursor getServerCursor() {
    return getInvoker().invoke(() -> getImpl().getServerCursor());
  }

  @Override
  default ServerAddress getServerAddress() {
    return getInvoker().invoke(() -> getImpl().getServerAddress());
  }
}
