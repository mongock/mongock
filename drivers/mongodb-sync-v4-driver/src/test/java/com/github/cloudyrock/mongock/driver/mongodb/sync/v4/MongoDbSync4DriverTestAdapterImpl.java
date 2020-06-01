package com.github.cloudyrock.mongock.driver.mongodb.sync.v4;

import com.github.cloudyrock.mongock.driver.mongodb.test.template.util.MongoDbDriverTestAdapter;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;

public class MongoDbSync4DriverTestAdapterImpl implements MongoDbDriverTestAdapter {

  private final MongoCollection<Document> collection;

  public MongoDbSync4DriverTestAdapterImpl(MongoCollection<Document> collection) {
    this.collection = collection;
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
