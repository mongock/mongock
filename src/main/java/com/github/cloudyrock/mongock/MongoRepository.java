package com.github.cloudyrock.mongock;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dieppa
 * @since 20.04.2018
 */
abstract class MongoRepository {

  private static final Logger logger = LoggerFactory.getLogger("MongoRepository");
  final MongoCollection<Document> collection;
  private final String[] uniqueFields;
  private MongoDatabase mongoDatabase;
  private boolean ensuredCollectionIndex = false;

  MongoRepository(MongoDatabase mongoDatabase, String collectionName, String[] uniqueFields) {
    this.mongoDatabase = mongoDatabase;
    this.collection = mongoDatabase.getCollection(collectionName);
    this.uniqueFields = uniqueFields;
  }

  void verifyDbConnectionAndEnsureIndex() throws MongockException {
    if (mongoDatabase == null) {
      throw new MongockException("Database is not connected. Mongock has thrown an unexpected error",
          new NullPointerException());
    }
    ensureChangeLogCollectionIndex();
  }

  private void ensureChangeLogCollectionIndex() {
    if (!this.ensuredCollectionIndex) {
      synchronized (this) {
        if (!this.ensuredCollectionIndex) {
          Document index = findRequiredChangeAndAuthorIndex();
          if (index == null) {
            createRequiredUniqueIndex();
            logger.debug("Index in collection {} was created", getColletionName() );
          } else if (!isUnique(index)) {
            dropIndex(index);
            createRequiredUniqueIndex();
            logger.debug("Index in collection {} was recreated", getColletionName());
          }
          this.ensuredCollectionIndex = true;
        }
      }
    }
  }

  void createRequiredUniqueIndex() {
    collection.createIndex(getIndexDocument(uniqueFields), new IndexOptions().unique(true));
  }

  Document findRequiredChangeAndAuthorIndex() {
    MongoCollection<Document> indexes = mongoDatabase.getCollection("system.indexes");

    return indexes.find(new Document()
        .append("ns", mongoDatabase.getName() + "." + getColletionName())
        .append("key", getIndexDocument(uniqueFields))
    ).first();
  }

  private String getColletionName() {
    return collection.getNamespace().getCollectionName();
  }

  private Document getIndexDocument(String[] uniqueFields) {
    Document indexDocument = new Document();
    for (String field : uniqueFields) {
      indexDocument.append(field, 1);
    }
    return indexDocument;
  }

  boolean isUnique(Document index) {
    Object unique = index.get("unique");
    if (unique != null && unique instanceof Boolean) {
      return (Boolean) unique;
    } else {
      return false;
    }
  }

  void dropIndex(Document index) {
    collection.dropIndex(index.get("name").toString());
  }

}
