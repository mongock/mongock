package io.mongock.driver.mongodb.v3;

import io.mongock.driver.mongodb.test.template.util.MongoDBDriverTestAdapter;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;

public class MongoDb3DriverTestAdapterImpl implements MongoDBDriverTestAdapter {

  private final MongoCollection<Document> collection;

  public MongoDb3DriverTestAdapterImpl(MongoCollection<Document> collection) {
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
