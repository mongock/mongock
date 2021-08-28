package io.mongock.driver.mongodb.springdata.v2.driver;

import io.mongock.driver.mongodb.test.template.util.MongoDBDriverTestAdapter;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;

public class SpringData2DriverTestAdapterImpl implements MongoDBDriverTestAdapter {

  private final MongoCollection<Document> collection;

  public SpringData2DriverTestAdapterImpl(MongoCollection<Document> collection) {
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
