package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.decorator.impl.MongoCursorDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.impl.MongoIterableDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.mongodb.Block;
import com.mongodb.Function;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;

import java.util.Collection;

public interface MongoIterableDecorator<T> extends MongoIterable<T> {

  MongoIterable<T> getImpl();

  MethodInvoker getInvoker();

  @Override
  default MongoCursor<T> iterator() {
    return new MongoCursorDecoratorImpl<>(getInvoker().invoke(() -> getImpl().iterator()), getInvoker());
  }

  @Override
  default T first() {
    return getInvoker().invoke(() -> getImpl().first());
  }

  @Override
  default <U> MongoIterable<U> map(Function<T, U> mapper) {
    return new MongoIterableDecoratorImpl<>(getInvoker().invoke(() -> getImpl().map(mapper)), getInvoker());
  }

  @Override
  default void forEach(Block<? super T> block) {
    getInvoker().invoke(() -> getImpl().forEach(block));
  }

  @Override
  default <A extends Collection<? super T>> A into(A target) {
    return getInvoker().invoke(() -> getImpl().into(target));
  }

  @Override
  default MongoIterable<T> batchSize(int batchSize) {
    return new MongoIterableDecoratorImpl<>(getInvoker().invoke(() -> getImpl().batchSize(batchSize)), getInvoker());
  }

  @Override
  default MongoCursor<T> cursor() {
    return getInvoker().invoke(() -> getImpl().cursor());
  }
}
