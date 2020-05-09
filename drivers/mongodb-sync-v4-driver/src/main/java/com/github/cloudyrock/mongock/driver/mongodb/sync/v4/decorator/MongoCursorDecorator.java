package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.decorator;

import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.MongoCursor;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.changock.migration.api.annotations.NonLockGuarded;

public interface MongoCursorDecorator<T> extends MongoCursor<T>, ChangockIterator<T> {

  MongoCursor<T> getImpl();

  LockGuardInvoker getInvoker();

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
  @NonLockGuarded
  default ServerAddress getServerAddress() {
    return getImpl().getServerAddress();
  }
}
