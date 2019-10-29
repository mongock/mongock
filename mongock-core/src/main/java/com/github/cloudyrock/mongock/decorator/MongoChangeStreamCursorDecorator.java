package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.mongodb.client.MongoChangeStreamCursor;
import com.mongodb.client.MongoCursor;
import org.bson.BsonDocument;

public interface MongoChangeStreamCursorDecorator<T> extends MongoCursorDecorator<T>, MongoChangeStreamCursor<T> {

  MongoChangeStreamCursor<T> getImpl();

  default BsonDocument getResumeToken() {
    return getInvoker().invoke(() -> getImpl().getResumeToken());
  }
}
