package com.github.cloudyrock.mongock.driver.mongodb.v3.decorator;

import com.github.cloudyrock.mongock.driver.mongodb.v3.decorator.impl.MongoCursorDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.v3.decorator.impl.MongoIterableDecoratorImpl;
import com.mongodb.Block;
import com.mongodb.Function;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.annotations.DecoratorDiverted;
import com.github.cloudyrock.mongock.annotations.NonLockGuarded;
import com.github.cloudyrock.mongock.annotations.NonLockGuardedType;

import java.util.Collection;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

public interface MongoIterableDecorator<T> extends MongoIterable<T> {

  MongoIterable<T> getImpl();

  LockGuardInvoker getInvoker();

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
    return new MongoCursorDecoratorImpl<>(getInvoker().invoke(() -> getImpl().cursor()), getInvoker());
  }


  @Override
  @NonLockGuarded
  default void forEach(Consumer<? super T> action) {
    getImpl().forEach(action);
  }

  @Override
  @DecoratorDiverted
  @NonLockGuarded(NonLockGuardedType.RETURN)
  default Spliterator<T> spliterator() {
    return Spliterators.spliteratorUnknownSize(iterator(), 0);
  }
}
