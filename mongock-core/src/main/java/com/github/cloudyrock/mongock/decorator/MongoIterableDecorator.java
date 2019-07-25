package com.github.cloudyrock.mongock.decorator;

import com.mongodb.Block;
import com.mongodb.Function;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;

import java.util.Collection;

public class MongoIterableDecorator<T> implements MongoIterable<T> {

  private final MongoIterable<T> impl;
  private final LockCheckInvoker checker;

  public MongoIterableDecorator(MongoIterable<T> implementation, LockCheckInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }


  @Override
  public MongoCursor<T> iterator() {
    return null;
  }

  @Override
  public T first() {
    return null;
  }

  @Override
  public <U> MongoIterable<U> map(Function<T, U> mapper) {
    return null;
  }

  @Override
  public void forEach(Block<? super T> block) {

  }

  @Override
  public <A extends Collection<? super T>> A into(A target) {
    return null;
  }

  @Override
  public MongoIterable<T> batchSize(int batchSize) {
    return null;
  }
}
