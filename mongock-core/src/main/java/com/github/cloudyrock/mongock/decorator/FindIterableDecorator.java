package com.github.cloudyrock.mongock.decorator;

import com.mongodb.CursorType;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Collation;
import org.bson.conversions.Bson;

import java.util.concurrent.TimeUnit;

/*
None of the methods in this class need to be re-wrapped in a decorator because it's returning the instance
itself.
They neither need to be encapsulated with the invoker because they don't hit the database.
 */
public interface FindIterableDecorator<T> extends MongoIterableDecorator<T>, FindIterable<T> {

  @Override
  FindIterable<T> getImpl();

  @Override
  default FindIterable<T> filter(Bson filter) {
    return getImpl().filter(filter);
  }

  @Override
  default FindIterable<T> limit(int limit) {
    return getImpl().limit(limit);
  }

  @Override
  default FindIterable<T> skip(int skip) {
    return getImpl().skip(skip);
  }

  @Override
  default FindIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
    return getImpl().maxTime(maxTime, timeUnit);
  }

  @Override
  default FindIterable<T> maxAwaitTime(long maxAwaitTime, TimeUnit timeUnit) {
    return getImpl().maxTime(maxAwaitTime, timeUnit);
  }

  @Override
  @Deprecated
  default FindIterable<T> modifiers(Bson modifiers) {
    return getImpl().modifiers(modifiers);
  }

  @Override
  default FindIterable<T> projection(Bson projection) {
    return getImpl().projection(projection);
  }

  @Override
  default FindIterable<T> sort(Bson sort) {
    return getImpl().sort(sort);
  }

  @Override
  default FindIterable<T> noCursorTimeout(boolean noCursorTimeout) {
    return getImpl().noCursorTimeout(noCursorTimeout);
  }

  @Override
  default FindIterable<T> oplogReplay(boolean oplogReplay) {
    return getImpl().oplogReplay(oplogReplay);
  }

  @Override
  default FindIterable<T> partial(boolean partial) {
    return getImpl().partial(partial);
  }

  @Override
  default FindIterable<T> cursorType(CursorType cursorType) {
    return getImpl().cursorType(cursorType);
  }

  @Override
  default FindIterable<T> batchSize(int batchSize) {
    return getImpl().batchSize(batchSize);
  }

  @Override
  default FindIterable<T> collation(Collation collation) {
    return getImpl().collation(collation);
  }

  @Override
  default FindIterable<T> comment(String comment) {
    return getImpl().comment(comment);
  }

  @Override
  default FindIterable<T> hint(Bson hint) {
    return getImpl().hint(hint);
  }

  @Override
  default FindIterable<T> max(Bson max) {
    return getImpl().max(max);
  }

  @Override
  default FindIterable<T> min(Bson min) {
    return getImpl().min(min);
  }

  @Override
  default FindIterable<T> maxScan(long maxScan) {
    return getImpl().maxScan(maxScan);
  }

  @Override
  default FindIterable<T> returnKey(boolean returnKey) {
    return getImpl().returnKey(returnKey);
  }

  @Override
  default FindIterable<T> showRecordId(boolean showRecordId) {
    return getImpl().showRecordId(showRecordId);
  }

  @Override
  default FindIterable<T> snapshot(boolean snapshot) {
    return getImpl().snapshot(snapshot);
  }
}
