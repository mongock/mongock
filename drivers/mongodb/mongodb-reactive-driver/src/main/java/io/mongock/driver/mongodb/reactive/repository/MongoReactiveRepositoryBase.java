package io.mongock.driver.mongodb.reactive.repository;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.common.EntityRepository;
import io.mongock.driver.api.common.RepositoryIndexable;
import io.mongock.driver.mongodb.reactive.util.SubscriberSync;
import io.mongock.driver.mongodb.reactive.util.MongoCollectionSync;
import io.mongock.driver.mongodb.reactive.util.MongoSubscriberSync;
import io.mongock.utils.field.FieldInstance;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class MongoReactiveRepositoryBase<DOMAIN_CLASS> implements EntityRepository<DOMAIN_CLASS, Document>, RepositoryIndexable {

  private final static Logger logger = LoggerFactory.getLogger(MongoReactiveRepositoryBase.class);
  private final static int INDEX_ENSURE_MAX_TRIES = 3;

  private final String[] uniqueFields;
  private boolean indexCreation = true;
  private boolean ensuredCollectionIndex = false;
  protected MongoCollectionSync collection;

  public MongoReactiveRepositoryBase(MongoCollection<Document> collection, String[] uniqueFields, ReadWriteConfiguration readWriteConfiguration) {
    MongoCollection<Document> enhancedCollection = collection
        .withReadConcern(readWriteConfiguration.getReadConcern())
        .withReadPreference(readWriteConfiguration.getReadPreference())
        .withWriteConcern(readWriteConfiguration.getWriteConcern());
    this.collection = new MongoCollectionSync(enhancedCollection);
    this.uniqueFields = uniqueFields;
  }

  @Override
  public synchronized void initialize() {
    if (!this.ensuredCollectionIndex) {
      ensureIndex(INDEX_ENSURE_MAX_TRIES);
      this.ensuredCollectionIndex = true;
    }
  }

  private void ensureIndex(int tryCounter) {
    if (tryCounter <= 0) {
      throw new MongockException("Max tries " + INDEX_ENSURE_MAX_TRIES + " index  creation");
    }
    if (!isIndexFine()) {
      if (!indexCreation) {
        throw new MongockException("Index creation not allowed, but not created or wrongly created for collection " + collection.getNamespace().getCollectionName());
      }
      cleanResidualUniqueKeys();
      if (!isRequiredIndexCreated()) {
        createRequiredUniqueIndex();
      }
      ensureIndex(tryCounter - 1);
    }
  }

  protected boolean isIndexFine() {
    return getResidualKeys().isEmpty() && isRequiredIndexCreated();
  }

  protected void cleanResidualUniqueKeys() {
    logger.debug("Removing residual uniqueKeys for collection [{}]", getCollectionName());
    getResidualKeys().stream()
        .peek(index -> logger.debug("Removed residual uniqueKey [{}] for collection [{}]", index.toString(), getCollectionName()))
        .forEach(this::dropIndex);
  }

  private List<Document> getResidualKeys() {
    SubscriberSync<Document> subscriber = new MongoSubscriberSync<>();
    return collection.listIndexes()
        .stream()
        .filter(this::doesNeedToBeRemoved)
        .collect(Collectors.toList());
  }

  protected boolean doesNeedToBeRemoved(Document index) {
    return !isIdIndex(index) && isUniqueIndex(index) && !isRightIndex(index);
  }

  protected boolean isIdIndex(Document index) {
    return ((Document) index.get("key")).get("_id") != null;
  }

  protected boolean isRequiredIndexCreated() {
    return collection.listIndexes()
        .stream()
        .anyMatch(this::isRightIndex);
  }

  protected void createRequiredUniqueIndex() {
    collection.createIndex(getIndexDocument(uniqueFields), new IndexOptions().unique(true));
    logger.debug("Index in collection [{}] was recreated", getCollectionName());
  }

  protected boolean isRightIndex(Document index) {
    final Document key = (Document) index.get("key");
    boolean keyContainsAllFields = Stream.of(uniqueFields).allMatch(uniqueField -> key.get(uniqueField) != null);
    boolean onlyTheseFields = key.size() == uniqueFields.length;
    return keyContainsAllFields && onlyTheseFields && isUniqueIndex(index);
  }

  protected boolean isUniqueIndex(Document index) {
    return index.getBoolean("unique", false);// checks it'unique
  }

  private String getCollectionName() {
    return collection.getNamespace().getCollectionName();
  }

  protected Document getIndexDocument(String[] uniqueFields) {
    final Document indexDocument = new Document();
    Stream.of(uniqueFields).forEach(field -> indexDocument.append(field, 1));
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

  @Override
  public void setIndexCreation(boolean indexCreation) {
    this.indexCreation = indexCreation;
  }

  /**
   * Only for testing
   */
  public void deleteAll() {
    collection.deleteMany(new Document());
  }
}
