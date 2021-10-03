package io.mongock.driver.mongodb.v3.decorator;

import io.mongock.driver.mongodb.v3.decorator.impl.ChangeStreamIterableDecoratorImpl;
import io.mongock.driver.mongodb.v3.decorator.impl.MongoChangeStreamCursorDecoratorImpl;
import io.mongock.driver.mongodb.v3.decorator.impl.MongoIterableDecoratorImpl;
import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.MongoChangeStreamCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import org.bson.BsonDocument;
import org.bson.BsonTimestamp;

import java.util.concurrent.TimeUnit;

public interface ChangeStreamIterableDecorator<T> extends MongoIterableDecorator<ChangeStreamDocument<T>>, ChangeStreamIterable<T> {

  @Override
  ChangeStreamIterable<T> getImpl();


  default MongoChangeStreamCursor<ChangeStreamDocument<T>> cursor() {
    return new MongoChangeStreamCursorDecoratorImpl<>(getInvoker().invoke(() -> getImpl().cursor()), getInvoker());
  }

  default ChangeStreamIterable<T> fullDocument(FullDocument var1){
    return new ChangeStreamIterableDecoratorImpl<>(getInvoker().invoke((()-> getImpl().fullDocument(var1))), getInvoker());
  }

  default ChangeStreamIterable<T> resumeAfter(BsonDocument var1){
    return new ChangeStreamIterableDecoratorImpl<>(getInvoker().invoke((()-> getImpl().resumeAfter(var1))), getInvoker());
  }

  default ChangeStreamIterable<T> batchSize(int var1) {
    return new ChangeStreamIterableDecoratorImpl<>(getInvoker().invoke((()-> getImpl().batchSize(var1))), getInvoker());
  }

  default ChangeStreamIterable<T> maxAwaitTime(long var1, TimeUnit var3){
    return new ChangeStreamIterableDecoratorImpl<>(getInvoker().invoke((()-> getImpl().maxAwaitTime(var1, var3))), getInvoker());
  }

  default ChangeStreamIterable<T> collation(Collation var1){
    return new ChangeStreamIterableDecoratorImpl<>(getInvoker().invoke((()-> getImpl().collation(var1))), getInvoker());
  }

  default <TDocument> MongoIterable<TDocument> withDocumentClass(Class<TDocument> var1){
    return new MongoIterableDecoratorImpl<>(getInvoker().invoke((()-> getImpl().withDocumentClass(var1))), getInvoker());
  }

  default ChangeStreamIterable<T> startAtOperationTime(BsonTimestamp var1){
    return new ChangeStreamIterableDecoratorImpl<>(getInvoker().invoke((()-> getImpl().startAtOperationTime(var1))), getInvoker());
  }

  default ChangeStreamIterable<T> startAfter(BsonDocument var1){
    return new ChangeStreamIterableDecoratorImpl<>(getInvoker().invoke((()-> getImpl().startAfter(var1))), getInvoker());
  }

}
