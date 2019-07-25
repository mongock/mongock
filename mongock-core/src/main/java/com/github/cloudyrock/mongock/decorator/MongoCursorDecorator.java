package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.decorator.util.LockCheckInvoker;
import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.MongoCursor;
public interface MongoCursorDecorator<T> extends MongoCursor<T> {

  MongoCursor getImpl();
  LockCheckInvoker getInvoker();

  @Override
  default void close() {

  }

  @Override
  default boolean hasNext() {
    return false;
  }

  @Override
  default T next() {
    return null;
  }

  @Override
  default T tryNext() {
    return null;
  }

  @Override
  default ServerCursor getServerCursor() {
    return null;
  }

  @Override
  default ServerAddress getServerAddress() {
    return null;
  }
}
