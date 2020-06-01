package com.github.cloudyrock.mongock.driver.mongodb.test.template.util;


import com.mongodb.client.model.IndexOptions;
import org.bson.Document;

public interface MongoDbDriverTestAdapter {

  void insertOne(Document document);
  long countDocuments(Document document);
  void createIndex(Document document, IndexOptions options);
}
