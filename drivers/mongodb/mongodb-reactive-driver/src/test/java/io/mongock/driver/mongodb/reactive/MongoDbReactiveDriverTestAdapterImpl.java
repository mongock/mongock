package io.mongock.driver.mongodb.reactive;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.mongock.driver.mongodb.reactive.util.MongoCollectionSync;
import io.mongock.driver.mongodb.reactive.util.MongoDBDriverTestAdapter;
import org.bson.Document;

public class MongoDbReactiveDriverTestAdapterImpl implements MongoDBDriverTestAdapter {

  private final MongoCollectionSync collection;

  public MongoDbReactiveDriverTestAdapterImpl(MongoCollection<Document> collection) {
    this.collection = new MongoCollectionSync(collection);
  }

  @Override
  public void insertOne(Document document) {
    collection.insertOne(document);
  }

  @Override
  public long countDocuments(Document document) {
    return collection.countDocuments(document);
  }

  @Override
  public void createIndex(Document document, IndexOptions options) {
    collection.createIndex(document, options);
  }
}
