package com.github.cloudyrock.mongock.driver.mongodb.v3.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import io.changock.utils.field.FieldInstance;
import io.changock.driver.core.common.Repository;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public abstract class Mongo3RepositoryBase<DOMAIN_CLASS> implements Repository<DOMAIN_CLASS, Document> {

  private static final Logger logger = LoggerFactory.getLogger("MongoRepository");

  private final String[] uniqueFields;
  private final String fullCollectionName;
  private boolean ensuredCollectionIndex = false;
  protected MongoCollection<Document> collection;

  public Mongo3RepositoryBase(MongoCollection<Document> collection, String[] uniqueFields) {
    this.collection = collection;
    this.fullCollectionName = collection.getNamespace().getDatabaseName() + "." + collection.getNamespace().getCollectionName();
    this.uniqueFields = uniqueFields;
  }

  @Override
  public synchronized void initialize() {
    if (!this.ensuredCollectionIndex) {
      cleanResidualUniqueKeys();
      if (isIndexCreationRequired()) {
        createRequiredUniqueIndex();
      }
      this.ensuredCollectionIndex = true;
    }
  }

  protected void cleanResidualUniqueKeys() {
    logger.debug("Removing residual uniqueKeys for collection [{}]", getCollectionName());
    StreamSupport.stream(collection.listIndexes().spliterator(), false)
        .filter(this::doesNeedToBeRemoved)
        .peek(index -> logger.debug("Removed residual uniqueKey [{}] for collection [{}]", index.toString(), getCollectionName()))
        .forEach(this::dropIndex);
  }

  protected boolean doesNeedToBeRemoved(Document index) {
    return !isIdIndex(index) && isUniqueIndex(index) && !isRightIndex(index);
  }

  protected boolean isIdIndex(Document index) {
    return (((Document) index.get("key")).getInteger("_id", 0) == 1);
  }

  protected boolean isIndexCreationRequired() {
    return StreamSupport.stream(collection.listIndexes().spliterator(), false).noneMatch(this::isRightIndex);
  }

  protected void createRequiredUniqueIndex() {
    collection.createIndex(getIndexDocument(uniqueFields), new IndexOptions().unique(true));
    logger.debug("Index in collection {} was recreated", getCollectionName());
  }

  protected boolean isRightIndex(Document index) {
    final Document key = (Document) index.get("key");
    boolean keyContainsAllFields = Stream.of(uniqueFields).allMatch(uniqueField -> key.getInteger(uniqueField, 0) == 1);
    boolean onlyTheseFields = key.size() == uniqueFields.length;
    return keyContainsAllFields && onlyTheseFields && isUniqueIndex(index);
  }

  protected boolean isUniqueIndex(Document index) {
    return fullCollectionName.equals(index.getString("ns")) && index.getBoolean("unique", false);
  }

  private String getCollectionName() {
    return collection.getNamespace().getCollectionName();
  }

  protected Document getIndexDocument(String[] uniqueFields) {
    final Document indexDocument = new Document();
    for (String field : uniqueFields) {
      indexDocument.append(field, 1);
    }
    return indexDocument;
  }

  protected void dropIndex(Document index) {
    collection.dropIndex(index.get("name").toString());
  }

  @Override
  public Document mapFieldInstances(List<FieldInstance> fieldInstanceList) {
    Document document = new Document();
    fieldInstanceList.forEach(def -> document.append(def.getName(), def.getValue()));
    return document;
  }


}
