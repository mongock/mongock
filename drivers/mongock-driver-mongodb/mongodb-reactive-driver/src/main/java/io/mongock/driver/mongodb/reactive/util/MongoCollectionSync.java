package io.mongock.driver.mongodb.reactive.util;

import com.mongodb.MongoNamespace;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;

public class MongoCollectionSync {

  private final MongoCollection<Document> collection;

  public MongoCollectionSync(MongoCollection<Document> collection) {
    this.collection = collection;
  }

  public MongoCollection<Document> getCollection() {
    return collection;
  }

  public MongoIterable<Document> find() {
    return find(new BsonDocument());
  }

  public MongoIterable<Document> find(Bson bson) {
    MongoSubscriber<Document> subscriber = new SubscriberSync<>();
    collection.find(bson).subscribe(subscriber);
    return subscriber.get();
  }

  public List<Document> listIndexes() {
    MongoSubscriber<Document> subscriber = new SubscriberSync<>();
    collection.listIndexes().subscribe(subscriber);
    return subscriber.get();
  }

  public String createIndex(Bson keys, IndexOptions indexOptions) {
    MongoSubscriber<String> subscriber = new SubscriberSync<>();
    collection.createIndex(keys, indexOptions).subscribe(subscriber);
    return subscriber.get().first();
  }

  public MongoNamespace getNamespace() {
    return collection.getNamespace();
  }

  public void dropIndex(String indexName) {
    MongoSubscriber<Void> subscriber = new SubscriberSync<>();
    collection.dropIndex(indexName).subscribe(subscriber);
    subscriber.await();
  }

  //TODO probably returns a List of DeleteResults
  public DeleteResult deleteMany(Bson filter) {
    MongoSubscriber<DeleteResult> subscriber = new SubscriberSync<>();
    collection.deleteMany(filter).subscribe(subscriber);
    return subscriber.get().first();
  }

  //TODO probably returns a List of UpdateResults
  public UpdateResult updateMany(Bson filter, Bson update, UpdateOptions updateOptions) {
    MongoSubscriber<UpdateResult> subscriber = new SubscriberSync<>();
    collection.updateMany(filter, update, updateOptions).subscribe(subscriber);
    return subscriber.get().first();
  }

  public UpdateResult replaceOne(ClientSession clientSession, Bson filter, Document replacement, ReplaceOptions replaceOptions) {
    MongoSubscriber<UpdateResult> subscriber = new SubscriberSync<>();
    collection.replaceOne(clientSession, filter, replacement, replaceOptions).subscribe(subscriber);
    return subscriber.get().first();
  }

  public UpdateResult replaceOne(Bson filter, Document replacement, ReplaceOptions replaceOptions) {
    MongoSubscriber<UpdateResult> subscriber = new SubscriberSync<>();
    collection.replaceOne(filter, replacement, replaceOptions).subscribe(subscriber);
    return subscriber.get().first();
  }

  public long countDocuments(Bson filter) {
    MongoSubscriber<Long> subscriber = new SubscriberSync<>();
    collection.countDocuments(filter).subscribe(subscriber);
    return subscriber.getFirst();
  }

  public InsertOneResult insertOne(Document document) {
    MongoSubscriber<InsertOneResult> subscriber = new SubscriberSync<>();
    collection.insertOne(document).subscribe(subscriber);
    return subscriber.getFirst();
  }


}
