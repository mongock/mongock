package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.decorator;

import io.changock.migration.api.annotations.NonLockGuarded;
import io.changock.migration.api.annotations.NonLockGuardedType;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;

import java.util.Iterator;
import java.util.function.Consumer;

public interface ChangockIterator<T> extends Iterator<T> {

  Iterator<T> getImpl();

  LockGuardInvoker getInvoker();

  @Override
  default boolean hasNext() {
    return getInvoker().invoke(() -> getImpl().hasNext());
  }

  @Override
  default T next() {
    return getInvoker().invoke(()-> getImpl().next());
  }

  @Override
  @NonLockGuarded(NonLockGuardedType.NONE)
  default void remove() {
    getImpl().remove();
  }


  @Override
  @NonLockGuarded(NonLockGuardedType.NONE)
  default void forEachRemaining(Consumer<? super T> action) {
    getImpl().forEachRemaining(action);
  }
}
