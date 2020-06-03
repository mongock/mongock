package com.github.cloudyrock.mongock.driver.mongodb.test.template.util;


import com.mongodb.client.model.IndexOptions;
import org.bson.Document;

import java.util.stream.Stream;

public interface MongoDbDriverTestAdapter {

  void insertOne(Document document);
  long countDocuments(Document document);
  void createIndex(Document document, IndexOptions options);
  default void createUniqueIndex(String... fields) {
    Document indexDocument = new Document();
    Stream.of(fields).forEach(field -> indexDocument.append(field, 1));
    createIndex(indexDocument, new IndexOptions().unique(true));

  }
}
