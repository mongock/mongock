package com.github.cloudyrock.mongock;

import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


abstract class MongoRepositoryBase implements Repository{

  private static final Logger logger = LoggerFactory.getLogger("MongoRepository");
  final MongoCollection<Document> collection;
  private final String[] uniqueFields;
  private final String fullCollectionName;
  private boolean ensuredCollectionIndex = false;

  MongoRepositoryBase(MongoDatabase mongoDatabase, String collectionName, String[] uniqueFields) {
    this.collection = mongoDatabase.getCollection(collectionName);
    this.fullCollectionName =
        collection.getNamespace().getDatabaseName() + "." + collection.getNamespace().getCollectionName();
    this.uniqueFields = uniqueFields;
  }

  public synchronized void initialize() {
    if (!this.ensuredCollectionIndex) {
      final Document index = findRequiredUniqueIndex();
      if (index == null) {
        createRequiredUniqueIndex();
        logger.debug("Index in collection {} was created", getCollectionName());
      } else if (!isUniqueIndex(index)) {
        dropIndex(index);
        createRequiredUniqueIndex();
        logger.debug("Index in collection {} was recreated", getCollectionName());
      }
      this.ensuredCollectionIndex = true;
    }

  }

  void createRequiredUniqueIndex() {
    collection.createIndex(getIndexDocument(uniqueFields), new IndexOptions().unique(true));
  }

  Document findRequiredUniqueIndex() {
    final ListIndexesIterable<Document> indexes = collection.listIndexes();
    for (Document index : indexes) {
      if (isUniqueIndex(index)) {
        return index;
      }
    }
    return null;
  }

  String getCollectionName() {
    return collection.getNamespace().getCollectionName();
  }

  Document getIndexDocument(String[] uniqueFields) {
    final Document indexDocument = new Document();
    for (String field : uniqueFields) {
      indexDocument.append(field, 1);
    }
    return indexDocument;
  }

  boolean isUniqueIndex(Document index) {
    final Document key = (Document) index.get("key");
    for (String uniqueField : uniqueFields) {
      if (key.getInteger(uniqueField, 0) != 1) {
        return false;
      }
    }
    return fullCollectionName.equals(index.getString("ns")) && index.getBoolean("unique", false);
  }

  void dropIndex(Document index) {
    collection.dropIndex(index.get("name").toString());
  }

}
