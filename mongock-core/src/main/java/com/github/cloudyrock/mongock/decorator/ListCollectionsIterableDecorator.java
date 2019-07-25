package com.github.cloudyrock.mongock.decorator;

import com.mongodb.Block;
import com.mongodb.Function;
import com.mongodb.client.ListCollectionsIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import org.bson.conversions.Bson;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class ListCollectionsIterableDecorator<T> implements ListCollectionsIterable<T> {

  public ListCollectionsIterableDecorator(ListCollectionsIterable<T> implementation, LockCheckInvoker lockerCheckInvoker) {
  }

  @Override
  public ListCollectionsIterable<T> filter(Bson filter) {
    return null;
  }

  @Override
  public ListCollectionsIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
    return null;
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
  public ListCollectionsIterable<T> batchSize(int batchSize) {
    return null;
  }
}
