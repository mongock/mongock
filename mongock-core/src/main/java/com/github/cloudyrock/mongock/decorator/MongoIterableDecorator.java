package com.github.cloudyrock.mongock.decorator;

import com.mongodb.Block;
import com.mongodb.Function;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;

import java.util.Collection;

public interface MongoIterableDecorator<T> extends MongoIterable<T> {

  MongoIterable<T> getInstance();
  LockCheckInvoker getCheckInvoker();


  @Override
  default MongoCursor<T> iterator() {
    return null;
  }

  @Override
  default T first() {
    return null;
  }

  @Override
  default <U> MongoIterable<U> map(Function<T, U> mapper) {
    return null;
  }

  @Override
  default void forEach(Block<? super T> block) {

  }

  @Override
  default <A extends Collection<? super T>> A into(A target) {
    return null;
  }

  @Override
  default MongoIterable<T> batchSize(int batchSize) {
    return null;
  }
}
