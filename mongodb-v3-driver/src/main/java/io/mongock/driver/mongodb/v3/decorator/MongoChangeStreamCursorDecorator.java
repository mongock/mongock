package io.mongock.driver.mongodb.v3.decorator;

import com.mongodb.client.MongoChangeStreamCursor;
import org.bson.BsonDocument;

public interface MongoChangeStreamCursorDecorator<T> extends MongoCursorDecorator<T>, MongoChangeStreamCursor<T> {

  MongoChangeStreamCursor<T> getImpl();

  default BsonDocument getResumeToken() {
    return getInvoker().invoke(() -> getImpl().getResumeToken());
  }
}
