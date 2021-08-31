package io.mongock.driver.mongodb.sync.v4.decorator;

import io.changock.migration.api.annotations.NonLockGuarded;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.MongoCursor;

public interface MongoCursorDecorator<T> extends MongoCursor<T>, MongockIterator<T> {

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
