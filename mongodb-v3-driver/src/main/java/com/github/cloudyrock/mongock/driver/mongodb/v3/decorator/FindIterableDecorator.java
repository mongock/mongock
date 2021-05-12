package com.github.cloudyrock.mongock.driver.mongodb.v3.decorator;

import com.github.cloudyrock.mongock.NonLockGuarded;
import com.github.cloudyrock.mongock.driver.mongodb.v3.decorator.impl.FindIterableDecoratorImpl;
import com.mongodb.CursorType;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Collation;
import org.bson.conversions.Bson;

import java.util.concurrent.TimeUnit;

/*
None of the methods in this class need to be re-wrapped in a guard because it's returning the instance
itself.
They neither need to be encapsulated with the invoker because they don't hit the database.
 */
public interface FindIterableDecorator<T> extends MongoIterableDecorator<T>, FindIterable<T> {

  @Override
  FindIterable<T> getImpl();

  @Override
  @NonLockGuarded
  default FindIterable<T> filter(Bson filter) {
    return new FindIterableDecoratorImpl<>(getImpl().filter(filter), getInvoker());
  }

  @Override
  @NonLockGuarded
  default FindIterable<T> limit(int limit) {
    return new FindIterableDecoratorImpl<>(getImpl().limit(limit), getInvoker());
  }

  @Override
  @NonLockGuarded
  default FindIterable<T> skip(int skip) {
    return new FindIterableDecoratorImpl<>(getImpl().skip(skip), getInvoker());
  }

  @Override
  @NonLockGuarded
  default FindIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
    return new FindIterableDecoratorImpl<>(getImpl().maxTime(maxTime, timeUnit), getInvoker());
  }

  @Override
  @NonLockGuarded
  default FindIterable<T> maxAwaitTime(long maxAwaitTime, TimeUnit timeUnit) {
    return new FindIterableDecoratorImpl<>(getImpl().maxAwaitTime(maxAwaitTime, timeUnit), getInvoker());
  }

  @Override
  @Deprecated
  @NonLockGuarded
  default FindIterable<T> modifiers(Bson modifiers) {
    return new FindIterableDecoratorImpl<>(getImpl().modifiers(modifiers), getInvoker());
  }

  @Override
  @NonLockGuarded
  default FindIterable<T> projection(Bson projection) {
    return new FindIterableDecoratorImpl<>(getImpl().projection(projection), getInvoker());
  }

  @Override
  @NonLockGuarded
  default FindIterable<T> sort(Bson sort) {
    return new FindIterableDecoratorImpl<>(getImpl().sort(sort), getInvoker());
  }

  @Override
  @NonLockGuarded
  default FindIterable<T> noCursorTimeout(boolean noCursorTimeout) {
    return new FindIterableDecoratorImpl<>(getImpl().noCursorTimeout(noCursorTimeout), getInvoker());
  }

  @Override
  @NonLockGuarded
  default FindIterable<T> oplogReplay(boolean oplogReplay) {
    return new FindIterableDecoratorImpl<>(getImpl().oplogReplay(oplogReplay), getInvoker());
  }

  @Override
  @NonLockGuarded
  default FindIterable<T> partial(boolean partial) {
    return new FindIterableDecoratorImpl<>(getImpl().partial(partial), getInvoker());
  }

  @Override
  @NonLockGuarded
  default FindIterable<T> cursorType(CursorType cursorType) {
    return new FindIterableDecoratorImpl<>(getImpl().cursorType(cursorType), getInvoker());
  }

  @Override
  @NonLockGuarded
  default FindIterable<T> batchSize(int batchSize) {
    return new FindIterableDecoratorImpl<>(getImpl().batchSize(batchSize), getInvoker());
  }

  @Override
  @NonLockGuarded
  default FindIterable<T> collation(Collation collation) {
    return new FindIterableDecoratorImpl<>(getImpl().collation(collation), getInvoker());
  }

  @Override
  @NonLockGuarded
  default FindIterable<T> comment(String comment) {
    return new FindIterableDecoratorImpl<>(getImpl().comment(comment), getInvoker());
  }

  @Override
  @NonLockGuarded
  default FindIterable<T> hint(Bson hint) {
    return new FindIterableDecoratorImpl<>(getImpl().hint(hint), getInvoker());
  }

  @Override
  @NonLockGuarded
  default FindIterable<T> max(Bson max) {
    return new FindIterableDecoratorImpl<>(getImpl().max(max), getInvoker());
  }

  @Override
  @NonLockGuarded
  default FindIterable<T> min(Bson min) {
    return new FindIterableDecoratorImpl<>(getImpl().min(min), getInvoker());
  }

  @Override
  @NonLockGuarded
  default FindIterable<T> maxScan(long maxScan) {
    return new FindIterableDecoratorImpl<>(getImpl().maxScan(maxScan), getInvoker());
  }

  @Override
  @NonLockGuarded
  default FindIterable<T> returnKey(boolean returnKey) {
    return new FindIterableDecoratorImpl<>(getImpl().returnKey(returnKey), getInvoker());
  }

  @Override
  @NonLockGuarded
  default FindIterable<T> showRecordId(boolean showRecordId) {
    return new FindIterableDecoratorImpl<>(getImpl().showRecordId(showRecordId), getInvoker());
  }

  @Override
  @NonLockGuarded
  default FindIterable<T> snapshot(boolean snapshot) {
    return new FindIterableDecoratorImpl<>(getImpl().snapshot(snapshot), getInvoker());
  }

  @Override
  @NonLockGuarded
  default FindIterable<T> hintString(String s) {
    return new FindIterableDecoratorImpl<>(getImpl().hintString(s), getInvoker());
  }
}
