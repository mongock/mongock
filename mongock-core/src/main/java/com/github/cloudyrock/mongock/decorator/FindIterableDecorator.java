package com.github.cloudyrock.mongock.decorator;

import com.mongodb.CursorType;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Collation;
import org.bson.conversions.Bson;

import java.util.concurrent.TimeUnit;

public interface FindIterableDecorator<T> extends MongoIterableDecorator<T>, FindIterable<T> {

  @Override
  FindIterable<T> getImpl();

  @Override
  default FindIterable<T> filter(Bson filter) {
    return null;
  }

  @Override
  default FindIterable<T> limit(int limit) {
    return null;
  }

  @Override
  default FindIterable<T> skip(int skip) {
    return null;
  }

  @Override
  default FindIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
    return null;
  }

  @Override
  default FindIterable<T> maxAwaitTime(long maxAwaitTime, TimeUnit timeUnit) {
    return null;
  }

  @Override
  default FindIterable<T> modifiers(Bson modifiers) {
    return null;
  }

  @Override
  default FindIterable<T> projection(Bson projection) {
    return null;
  }

  @Override
  default FindIterable<T> sort(Bson sort) {
    return null;
  }

  @Override
  default FindIterable<T> noCursorTimeout(boolean noCursorTimeout) {
    return null;
  }

  @Override
  default FindIterable<T> oplogReplay(boolean oplogReplay) {
    return null;
  }

  @Override
  default FindIterable<T> partial(boolean partial) {
    return null;
  }

  @Override
  default FindIterable<T> cursorType(CursorType cursorType) {
    return null;
  }

  @Override
  default FindIterable<T> batchSize(int batchSize) {
    return null;
  }

  @Override
  default FindIterable<T> collation(Collation collation) {
    return null;
  }

  @Override
  default FindIterable<T> comment(String comment) {
    return null;
  }

  @Override
  default FindIterable<T> hint(Bson hint) {
    return null;
  }

  @Override
  default FindIterable<T> max(Bson max) {
    return null;
  }

  @Override
  default FindIterable<T> min(Bson min) {
    return null;
  }

  @Override
  default FindIterable<T> maxScan(long maxScan) {
    return null;
  }

  @Override
  default FindIterable<T> returnKey(boolean returnKey) {
    return null;
  }

  @Override
  default FindIterable<T> showRecordId(boolean showRecordId) {
    return null;
  }

  @Override
  default FindIterable<T> snapshot(boolean snapshot) {
    return null;
  }
}
