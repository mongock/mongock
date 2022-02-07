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
    SubscriberSync<Document> subscriber = new MongoSubscriberSync<>();
    collection.find(bson).subscribe(subscriber);
    return subscriber.get();
  }

  public List<Document> listIndexes() {
    SubscriberSync<Document> subscriber = new MongoSubscriberSync<>();
    collection.listIndexes().subscribe(subscriber);
    return subscriber.get();
  }

  public String createIndex(Bson keys, IndexOptions indexOptions) {
    SubscriberSync<String> subscriber = new MongoSubscriberSync<>();
    collection.createIndex(keys, indexOptions).subscribe(subscriber);
    return subscriber.get().first();
  }

  public MongoNamespace getNamespace() {
    return collection.getNamespace();
  }

  public void dropIndex(String indexName) {
    SubscriberSync<Void> subscriber = new MongoSubscriberSync<>();
    collection.dropIndex(indexName).subscribe(subscriber);
    subscriber.await();
  }

  //TODO probably returns a List of DeleteResults
  public DeleteResult deleteMany(Bson filter) {
    SubscriberSync<DeleteResult> subscriber = new MongoSubscriberSync<>();
    collection.deleteMany(filter).subscribe(subscriber);
    return subscriber.get().first();
  }

  //TODO probably returns a List of UpdateResults
  public UpdateResult updateMany(Bson filter, Bson update, UpdateOptions updateOptions) {
    SubscriberSync<UpdateResult> subscriber = new MongoSubscriberSync<>();
    collection.updateMany(filter, update, updateOptions).subscribe(subscriber);
    return subscriber.get().first();
  }

  public UpdateResult replaceOne(ClientSession clientSession, Bson filter, Document replacement, ReplaceOptions replaceOptions) {
    SubscriberSync<UpdateResult> subscriber = new MongoSubscriberSync<>();
    collection.replaceOne(clientSession, filter, replacement, replaceOptions).subscribe(subscriber);
    return subscriber.get().first();
  }

  public UpdateResult replaceOne(Bson filter, Document replacement, ReplaceOptions replaceOptions) {
    SubscriberSync<UpdateResult> subscriber = new MongoSubscriberSync<>();
    collection.replaceOne(filter, replacement, replaceOptions).subscribe(subscriber);
    return subscriber.get().first();
  }

  public long countDocuments(Bson filter) {
    SubscriberSync<Long> subscriber = new MongoSubscriberSync<>();
    collection.countDocuments(filter).subscribe(subscriber);
    return subscriber.getFirst();
  }

  public InsertOneResult insertOne(Document document) {
    SubscriberSync<InsertOneResult> subscriber = new MongoSubscriberSync<>();
    collection.insertOne(document).subscribe(subscriber);
    return subscriber.getFirst();
  }


}
