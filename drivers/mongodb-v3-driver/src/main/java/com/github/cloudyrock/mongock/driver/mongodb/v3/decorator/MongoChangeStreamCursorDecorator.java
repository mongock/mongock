package com.github.cloudyrock.mongock.driver.mongodb.v3.decorator;

import com.mongodb.client.MongoChangeStreamCursor;
import io.changock.migration.api.annotations.NonLockGuarded;
import org.bson.BsonDocument;

public interface MongoChangeStreamCursorDecorator<T> extends MongoCursorDecorator<T>, MongoChangeStreamCursor<T> {

  MongoChangeStreamCursor<T> getImpl();

  default BsonDocument getResumeToken() {
    return getInvoker().invoke(() -> getImpl().getResumeToken());
  }
}
