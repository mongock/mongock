package com.github.cloudyrock.mongock;

import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


abstract class MongoRepositoryBase implements Repository{

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
      if(isIndexCreationRequired()) {
        createRequiredUniqueIndex();
      }
      this.ensuredCollectionIndex = true;
    }
  }

//  public synchronized void initialize() {
//    if (!this.ensuredCollectionIndex) {
//      final Document index = findRequiredUniqueIndex();
//      if (index == null) {
//        createRequiredUniqueIndex();
//        logger.debug("Index in collection {} was created", getCollectionName());
//      } else if (!isRightIndex(index)) {
//        dropIndex(index);
//        createRequiredUniqueIndex();
//        logger.debug("Index in collection {} was recreated", getCollectionName());
//      }
//      this.ensuredCollectionIndex = true;
//    }
//  }


  private void cleanResidualUniqueKeys() {
    logger.debug("Removing residual uniqueKeys for collection [{}]", getCollectionName());
    final ListIndexesIterable<Document> indexes = collection.listIndexes();
    for (Document index : indexes) {
      if (needsToBeRemoved(index)) {
        dropIndex(index);
        logger.debug("Removed residual uniqueKey [{}] for collection [{}]", index.toString(), getCollectionName());
      } else {
        logger.debug("Keeping right uniqueKey [{}] for collection [{}]", index.toString(), getCollectionName());
      }
    }
  }

  private boolean needsToBeRemoved(Document index) {
    return isNotIdIndex(index) && isUniqueIndex(index) && !isRightIndex(index);
  }

  private boolean isNotIdIndex(Document index) {
    return !(((Document) index.get("key")).getInteger("_id", 0) == 1);

  }

  private boolean isIndexCreationRequired() {
    return StreamSupport.stream(collection.listIndexes().spliterator(), false)
        .noneMatch(this::isRightIndex);
  }

  void createRequiredUniqueIndex() {
    collection.createIndex(getIndexDocument(uniqueFields), new IndexOptions().unique(true));
    logger.debug("Index in collection {} was recreated", getCollectionName());
  }

  //todo what if I require 2 fields, but the current key contains those two plus more?? -> that wouldn't be right
  boolean isRightIndex(Document index) {
    final Document key = (Document) index.get("key");
    Set<String> keySet = key.keySet();
    for (String uniqueField : uniqueFields) {
      if (key.getInteger(uniqueField, 0) != 1) {
        return false;
      }
    }
    return isUniqueIndex(index);
  }

  private boolean isUniqueIndex(Document index) {
    return fullCollectionName.equals(index.getString("ns")) && index.getBoolean("unique", false);
  }



  Document findRequiredUniqueIndex() {
    final ListIndexesIterable<Document> indexes = collection.listIndexes();
    for (Document index : indexes) {
      if (isRightIndex(index)) {
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



  void dropIndex(Document index) {
    collection.dropIndex(index.get("name").toString());
  }

}
