package com.github.cloudyrock.mongock;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;


abstract class MongoRepositoryBase implements Repository {

  private static final Logger logger = LoggerFactory.getLogger("MongoRepository");
  final MongoCollection<Document> collection;
  private final String[] uniqueFields;
  private final String fullCollectionName;
  private boolean ensuredCollectionIndex = false;

  MongoRepositoryBase(MongoDatabase mongoDatabase, String collectionName, String[] uniqueFields) {
    this.collection = mongoDatabase.getCollection(collectionName);
    this.fullCollectionName = collection.getNamespace().getDatabaseName() + "." + collection.getNamespace().getCollectionName();
    this.uniqueFields = uniqueFields;
  }

  public synchronized void initialize() {
    if (!this.ensuredCollectionIndex) {
      cleanResidualUniqueKeys();
      if (isIndexCreationRequired()) {
        createRequiredUniqueIndex();
      }
      this.ensuredCollectionIndex = true;
    }
  }

  private void cleanResidualUniqueKeys() {
    logger.debug("Removing residual uniqueKeys for collection [{}]", getCollectionName());
    StreamSupport.stream(collection.listIndexes().spliterator(), false)
        .filter(this::doesNeedToBeRemoved)
        .peek(index -> logger.debug("Removed residual uniqueKey [{}] for collection [{}]", index.toString(), getCollectionName()))
        .forEach(this::dropIndex);
  }

  private boolean doesNeedToBeRemoved(Document index) {
    return !isIdIndex(index) && isUniqueIndex(index) && !isRightIndex(index);
  }

  private boolean isIdIndex(Document index) {
    return (((Document) index.get("key")).getInteger("_id", 0) == 1);
  }

  boolean isIndexCreationRequired() {
    return StreamSupport.stream(collection.listIndexes().spliterator(), false).noneMatch(this::isRightIndex);
  }

  void createRequiredUniqueIndex() {
    collection.createIndex(getIndexDocument(uniqueFields), new IndexOptions().unique(true));
    logger.debug("Index in collection {} was recreated", getCollectionName());
  }

  boolean isRightIndex(Document index) {
    final Document key = (Document) index.get("key");
    boolean keyContainsAllFields = Stream.of(uniqueFields).allMatch(uniqueField -> key.getInteger(uniqueField, 0) == 1);
    boolean onlyTheseFields = key.size() == uniqueFields.length;
    return keyContainsAllFields && onlyTheseFields && isUniqueIndex(index);
  }

  private boolean isUniqueIndex(Document index) {
    return fullCollectionName.equals(index.getString("ns")) && index.getBoolean("unique", false);
  }

  private String getCollectionName() {
    return collection.getNamespace().getCollectionName();
  }

  private Document getIndexDocument(String[] uniqueFields) {
    final Document indexDocument = new Document();
    for (String field : uniqueFields) {
      indexDocument.append(field, 1);
    }
    return indexDocument;
  }

  void dropIndex(Document index) {
    collection.dropIndex(index.get("name").toString());
  }
}
