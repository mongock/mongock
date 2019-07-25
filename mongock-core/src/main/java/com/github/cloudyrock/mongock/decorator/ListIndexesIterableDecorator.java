package com.github.cloudyrock.mongock.decorator;

import com.mongodb.Block;
import com.mongodb.Function;
import com.mongodb.client.ListIndexesIterable;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class ListIndexesIterableDecorator<T> implements ListIndexesIterable<T>, MongoIterableDecorator<T> {

  @Override
  public MongoCursorDecorator<T> iterator() {
    return null;
  }

  @Override
  public T first() {
    return null;
  }

  @Override
  public <U> MongoIterableDecorator<U> map(Function<T, U> mapper) {
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
  public ListIndexesIterableDecorator<T> maxTime(long maxTime, TimeUnit timeUnit) {
    return null;
  }

  @Override
  public ListIndexesIterableDecorator<T> batchSize(int batchSize) {
    return null;
  }
}
