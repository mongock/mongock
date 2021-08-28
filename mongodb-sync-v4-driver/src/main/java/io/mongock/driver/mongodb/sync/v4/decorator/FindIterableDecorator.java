package io.mongock.driver.mongodb.sync.v4.decorator;

import com.github.cloudyrock.mongock.NonLockGuarded;
import io.mongock.driver.mongodb.sync.v4.decorator.impl.FindIterableDecoratorImpl;
import com.mongodb.CursorType;
import com.mongodb.ExplainVerbosity;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Collation;
import org.bson.Document;
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
  default FindIterable<T> returnKey(boolean returnKey) {
    return new FindIterableDecoratorImpl<>(getImpl().returnKey(returnKey), getInvoker());
  }

  @Override
  @NonLockGuarded
  default FindIterable<T> showRecordId(boolean showRecordId) {
    return new FindIterableDecoratorImpl<>(getImpl().showRecordId(showRecordId), getInvoker());
  }

  //from v4.0.2
  @Override
  @NonLockGuarded
  default FindIterable<T> hintString(String s) {
    return new FindIterableDecoratorImpl<>(getImpl().hintString(s), getInvoker());
  }

  //from v4.1.2
  @Override
  @NonLockGuarded
  default FindIterable<T> allowDiskUse(Boolean aBoolean) {
    return new FindIterableDecoratorImpl<>(getImpl().allowDiskUse(aBoolean), getInvoker());
  }

  @Override
  @NonLockGuarded
  default Document explain() {
    return getImpl().explain();
  }

  @Override
  @NonLockGuarded
  default Document explain(ExplainVerbosity explainVerbosity) {
    return getImpl().explain(explainVerbosity);
  }

  @Override
  @NonLockGuarded
  default <E> E explain(Class<E> aClass) {
    return getImpl().explain(aClass);
  }

  @Override
  @NonLockGuarded
  default <E> E explain(Class<E> aClass, ExplainVerbosity explainVerbosity) {
    return getImpl().explain(aClass, explainVerbosity);
  }

}
