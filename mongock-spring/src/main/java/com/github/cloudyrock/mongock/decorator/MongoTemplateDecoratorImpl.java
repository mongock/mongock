package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.decorator.impl.BulkOperationsDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.impl.IndexOperationsDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.impl.MongoCollectionDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.impl.ScriptOperationsDecoratorImpl;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.util.CloseableIterator;
import org.springframework.data.util.Optionals;
import org.springframework.util.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.springframework.data.mongodb.core.query.SerializationUtils.serializeToJsonSafely;

  public class MongoTemplateDecoratorImpl extends MongoTemplate {
  private final com.github.cloudyrock.mongock.decorator.util.MethodInvoker methodInvoker;

  public MongoTemplateDecoratorImpl(MongoClient mongoClient, String databaseName, com.github.cloudyrock.mongock.decorator.util.MethodInvoker methodInvoker) {
    super(mongoClient, databaseName);
    this.methodInvoker = methodInvoker;
  }

  public MongoTemplateDecoratorImpl(MongoDbFactory mongoDbFactory, com.github.cloudyrock.mongock.decorator.util.MethodInvoker methodInvoker) {
    super(mongoDbFactory);
    this.methodInvoker = methodInvoker;
  }

  public MongoTemplateDecoratorImpl(MongoDbFactory mongoDbFactory, MongoConverter mongoConverter, com.github.cloudyrock.mongock.decorator.util.MethodInvoker methodInvoker) {
    super(mongoDbFactory, mongoConverter);
    this.methodInvoker = methodInvoker;
  }
  
  private com.github.cloudyrock.mongock.decorator.util.MethodInvoker getInvoker() {
    return methodInvoker;
  }

  
  @Override
  public void setWriteResultChecking( WriteResultChecking resultChecking) {
    super.setWriteResultChecking(resultChecking);
  }

  @Override
  public void setWriteConcern( WriteConcern writeConcern) {
    super.setWriteConcern(writeConcern);
  }


  @Override
  public void setWriteConcernResolver( WriteConcernResolver writeConcernResolver) {
    super.setWriteConcernResolver(writeConcernResolver);
  }

 
  @Override
  public void setReadPreference( ReadPreference readPreference) {
    super.setReadPreference(readPreference);
  }

  
  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    super.setApplicationContext(applicationContext);
  }

  @Override
  public MongoConverter getConverter() {
    return super.getConverter();
  }
  
  @Override
  public <T> CloseableIterator<T> stream(final Query query, final Class<T> entityType) {
    return getInvoker().invoke(() -> super.stream(query, entityType));
  }

  @Override
  public <T> CloseableIterator<T> stream(final Query query, final Class<T> entityType, final String collectionName) {
    return getInvoker().invoke(() -> super.stream(query, entityType, collectionName));
  }

  @Override
  public String getCollectionName(Class<?> entityClass) {
    return super.getCollectionName(entityClass);
  }

  @Override
  public Document executeCommand(final String jsonCommand) {
    return getInvoker().invoke(()-> super.executeCommand(jsonCommand));
  }
  
  @Override
  public Document executeCommand(final Document command) {
    return getInvoker().invoke(()-> super.executeCommand(command));
  }
  
  @Override
  public Document executeCommand(Document command,  ReadPreference readPreference) {
    return getInvoker().invoke(()-> super.executeCommand(command, readPreference));
  }

  @Override
  public void executeQuery(Query query, String collectionName, DocumentCallbackHandler dch) {
    getInvoker().invoke(()-> super.executeQuery(query, collectionName, dch));
  }

  @Override
  public <T> T execute(DbCallback<T> action) {
    return getInvoker().invoke(()-> super.execute(action));
  }

  @Override
  public <T> T execute(Class<?> entityClass, CollectionCallback<T> callback) {
    return getInvoker().invoke(()-> super.execute(entityClass, callback));
  }

  @Override
  public <T> T execute(String collectionName, CollectionCallback<T> callback) {
    return getInvoker().invoke(()-> super.execute(collectionName, callback));
  }

  @Override
  public <T> MongoCollection<Document> createCollection(Class<T> entityClass) {
    return new MongoCollectionDecoratorImpl<>(getInvoker().invoke(()-> super.createCollection(entityClass)), methodInvoker);
  }


  @Override
  public <T> MongoCollection<Document> createCollection(Class<T> entityClass, CollectionOptions collectionOptions) {
    return new MongoCollectionDecoratorImpl<>(getInvoker().invoke(()-> super.createCollection(entityClass, collectionOptions)), methodInvoker);
  }

  @Override
  public MongoCollection<Document> createCollection(final String collectionName) {
    return new MongoCollectionDecoratorImpl<>(getInvoker().invoke(()-> super.createCollection(collectionName)), methodInvoker);
  }

  @Override
  public MongoCollection<Document> createCollection(final String collectionName, final  CollectionOptions collectionOptions) {
    return new MongoCollectionDecoratorImpl<>(getInvoker().invoke(()-> super.createCollection(collectionName, collectionOptions)), methodInvoker);
  }

  @Override
  public MongoCollection<Document> getCollection(final String collectionName) {
    return new MongoCollectionDecoratorImpl<>(getInvoker().invoke(()-> super.getCollection(collectionName)), methodInvoker);
  }

  @Override
  public <T> boolean collectionExists(Class<T> entityClass) {
    return getInvoker().invoke(()-> super.collectionExists(entityClass));
  }

  @Override
  public boolean collectionExists(final String collectionName) {
    return getInvoker().invoke(()-> super.collectionExists(collectionName));
  }

  @Override
  public <T> void dropCollection(Class<T> entityClass) {
    getInvoker().invoke(()-> super.dropCollection(entityClass));
  }

  @Override
  public void dropCollection(String collectionName) {
    getInvoker().invoke(()-> super.dropCollection(collectionName));
  }
  @Override
  public IndexOperations indexOps(String collectionName) {
    return new IndexOperationsDecoratorImpl(getInvoker().invoke(()-> super.indexOps(collectionName)), methodInvoker);
  }

  @Override
  public IndexOperations indexOps(Class<?> entityClass) {
    return new IndexOperationsDecoratorImpl(getInvoker().invoke(()-> super.indexOps(entityClass)), methodInvoker);
  }

  @Override
  public BulkOperations bulkOps(BulkOperations.BulkMode bulkMode, String collectionName) {
    return new BulkOperationsDecoratorImpl(getInvoker().invoke(()-> super.bulkOps(bulkMode, collectionName)), methodInvoker);
  }

  @Override
  public BulkOperations bulkOps(BulkOperations.BulkMode bulkMode, Class<?> entityClass) {
    return new BulkOperationsDecoratorImpl(getInvoker().invoke(()-> super.bulkOps(bulkMode, entityClass)), methodInvoker);
  }

  @Override
  public BulkOperations bulkOps(BulkOperations.BulkMode mode,  Class<?> entityType, String collectionName) {
    return new BulkOperationsDecoratorImpl(getInvoker().invoke(()-> super.bulkOps(mode, entityType, collectionName)), methodInvoker);
  }

  @Override
  public ScriptOperations scriptOps() {
    return new ScriptOperationsDecoratorImpl(getInvoker().invoke(()-> super.scriptOps()), methodInvoker);
  }

  // Find methods that take a Query to express the query and that return a single object.

  
  @Override
  public <T> T findOne(Query query, Class<T> entityClass) {
    return findOne(query, entityClass, determineCollectionName(entityClass));
  }

  
  @Override
  public <T> T findOne(Query query, Class<T> entityClass, String collectionName) {

  }

  @Override
  public boolean exists(Query query, Class<?> entityClass) {
    return exists(query, entityClass, determineCollectionName(entityClass));
  }

  @Override
  public boolean exists(Query query, String collectionName) {
    return exists(query, null, collectionName);
  }

  @Override
  public boolean exists(Query query,  Class<?> entityClass, String collectionName) {

  }

  // Find methods that take a Query to express the query and that return a List of objects.

  /*
   * (non-Javadoc)
   * @see org.springframework.data.mongodb.core.MongoOperations#findOne(org.springframework.data.mongodb.core.query.Query, java.lang.Class)
   */
  @Override
  public <T> List<T> find(Query query, Class<T> entityClass) {
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.data.mongodb.core.MongoOperations#findOne(org.springframework.data.mongodb.core.query.Query, java.lang.Class, java.lang.String)
   */
  @Override
  public <T> List<T> find(Query query, Class<T> entityClass, String collectionName) {

  }

  
  @Override
  public <T> T findById(Object id, Class<T> entityClass) {
  }

  
  @Override
  public <T> T findById(Object id, Class<T> entityClass, String collectionName) {

    Assert.notNull(id, "Id must not be null!");
    Assert.notNull(entityClass, "EntityClass must not be null!");
    Assert.notNull(collectionName, "CollectionName must not be null!");

    MongoPersistentEntity<?> persistentEntity = mappingContext.getPersistentEntity(entityClass);
    String idKey = ID_FIELD;

    if (persistentEntity != null) {
      if (persistentEntity.getIdProperty() != null) {
        idKey = persistentEntity.getIdProperty().getName();
      }
    }

    return doFindOne(collectionName, new Document(idKey, id), new Document(), entityClass);
  }

  @Override
  public <T> GeoResults<T> geoNear(NearQuery near, Class<T> entityClass) {
    return geoNear(near, entityClass, determineCollectionName(entityClass));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> GeoResults<T> geoNear(NearQuery near, Class<T> domainType, String collectionName) {
    return geoNear(near, domainType, collectionName, domainType);
  }

  @Override
  public <T> GeoResults<T> geoNear(NearQuery near, Class<?> domainType, String collectionName, Class<T> returnType) {

    if (near == null) {
      throw new InvalidDataAccessApiUsageException("NearQuery must not be null!");
    }

    if (domainType == null) {
      throw new InvalidDataAccessApiUsageException("Entity class must not be null!");
    }

    Assert.notNull(collectionName, "CollectionName must not be null!");
    Assert.notNull(returnType, "ReturnType must not be null!");

    String collection = StringUtils.hasText(collectionName) ? collectionName : determineCollectionName(domainType);
    Document nearDocument = near.toDocument();

    Document command = new Document("geoNear", collection);
    command.putAll(nearDocument);

    if (nearDocument.containsKey("query")) {
      Document query = (Document) nearDocument.get("query");
      command.put("query", queryMapper.getMappedObject(query, getPersistentEntity(domainType)));
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Executing geoNear using: {} for class: {} in collection: {}", serializeToJsonSafely(command),
          domainType, collectionName);
    }

    Document commandResult = executeCommand(command, this.readPreference);
    List<Object> results = (List<Object>) commandResult.get("results");
    results = results == null ? Collections.emptyList() : results;

    MongoTemplate.DocumentCallback<GeoResult<T>> callback = new MongoTemplate.GeoNearResultDocumentCallback<T>(
        new MongoTemplate.ProjectingReadCallback<>(mongoConverter, domainType, returnType, collectionName), near.getMetric());
    List<GeoResult<T>> result = new ArrayList<GeoResult<T>>(results.size());

    int index = 0;
    long elementsToSkip = near.getSkip() != null ? near.getSkip() : 0;

    for (Object element : results) {

      /*
       * As MongoDB currently (2.4.4) doesn't support the skipping of elements in near queries
       * we skip the elements ourselves to avoid at least the document 2 object mapping overhead.
       *
       * @see <a href="https://jira.mongodb.org/browse/SERVER-3925">MongoDB Jira: SERVER-3925</a>
       */
      if (index >= elementsToSkip) {
        result.add(callback.doWith((Document) element));
      }
      index++;
    }

    if (elementsToSkip > 0) {
      // as we skipped some elements we have to calculate the averageDistance ourselves:
      return new GeoResults<T>(result, near.getMetric());
    }

    GeoCommandStatistics stats = GeoCommandStatistics.from(commandResult);
    return new GeoResults<T>(result, new Distance(stats.getAverageDistance(), near.getMetric()));
  }

  
  @Override
  public <T> T findAndModify(Query query, Update update, Class<T> entityClass) {
    return findAndModify(query, update, new FindAndModifyOptions(), entityClass, determineCollectionName(entityClass));
  }

  
  @Override
  public <T> T findAndModify(Query query, Update update, Class<T> entityClass, String collectionName) {
    return findAndModify(query, update, new FindAndModifyOptions(), entityClass, collectionName);
  }

  
  @Override
  public <T> T findAndModify(Query query, Update update, FindAndModifyOptions options, Class<T> entityClass) {
    return findAndModify(query, update, options, entityClass, determineCollectionName(entityClass));
  }

  
  @Override
  public <T> T findAndModify(Query query, Update update, FindAndModifyOptions options, Class<T> entityClass,
                             String collectionName) {

    Assert.notNull(query, "Query must not be null!");
    Assert.notNull(update, "Update must not be null!");
    Assert.notNull(options, "Options must not be null!");
    Assert.notNull(entityClass, "EntityClass must not be null!");
    Assert.notNull(collectionName, "CollectionName must not be null!");

    FindAndModifyOptions optionsToUse = FindAndModifyOptions.of(options);

    Optionals.ifAllPresent(query.getCollation(), optionsToUse.getCollation(), (l, r) -> {
      throw new IllegalArgumentException(
          "Both Query and FindAndModifyOptions define a collation. Please provide the collation only via one of the two.");
    });

    query.getCollation().ifPresent(optionsToUse::collation);

    return doFindAndModify(collectionName, query.getQueryObject(), query.getFieldsObject(),
        getMappedSortObject(query, entityClass), entityClass, update, optionsToUse);
  }

  // Find methods that take a Query to express the query and that return a single object that is also removed from the
  // collection in the database.

  
  @Override
  public <T> T findAndRemove(Query query, Class<T> entityClass) {
    return findAndRemove(query, entityClass, determineCollectionName(entityClass));
  }

  
  @Override
  public <T> T findAndRemove(Query query, Class<T> entityClass, String collectionName) {

    Assert.notNull(query, "Query must not be null!");
    Assert.notNull(entityClass, "EntityClass must not be null!");
    Assert.notNull(collectionName, "CollectionName must not be null!");

    return doFindAndRemove(collectionName, query.getQueryObject(), query.getFieldsObject(),
        getMappedSortObject(query, entityClass), query.getCollation().orElse(null), entityClass);
  }

  @Override
  public long count(Query query, Class<?> entityClass) {

    Assert.notNull(entityClass, "Entity class must not be null!");
    return count(query, entityClass, determineCollectionName(entityClass));
  }

  @Override
  public long count(final Query query, String collectionName) {
    return count(query, null, collectionName);
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.data.mongodb.core.MongoOperations#count(org.springframework.data.mongodb.core.query.Query, java.lang.Class, java.lang.String)
   */
  @Override
  public long count(Query query,  Class<?> entityClass, String collectionName) {

    Assert.notNull(query, "Query must not be null!");
    Assert.hasText(collectionName, "Collection name must not be null or empty!");

    Document document = queryMapper.getMappedObject(query.getQueryObject(),
        Optional.ofNullable(entityClass).map(it -> mappingContext.getPersistentEntity(entityClass)));

    return execute(collectionName, collection -> collection.count(document));
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.data.mongodb.core.MongoOperations#insert(java.lang.Object)
   */
  @Override
  public void insert(Object objectToSave) {

    Assert.notNull(objectToSave, "ObjectToSave must not be null!");

    ensureNotIterable(objectToSave);
    insert(objectToSave, determineEntityCollectionName(objectToSave));
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.data.mongodb.core.MongoOperations#insert(java.lang.Object, java.lang.String)
   */
  @Override
  public void insert(Object objectToSave, String collectionName) {

    Assert.notNull(objectToSave, "ObjectToSave must not be null!");
    Assert.notNull(collectionName, "CollectionName must not be null!");

    ensureNotIterable(objectToSave);
    doInsert(collectionName, objectToSave, this.mongoConverter);
  }
  


  @Override
  public void insert(Collection<? extends Object> batchToSave, Class<?> entityClass) {

    Assert.notNull(batchToSave, "BatchToSave must not be null!");

    doInsertBatch(determineCollectionName(entityClass), batchToSave, this.mongoConverter);
  }

  @Override
  public void insert(Collection<? extends Object> batchToSave, String collectionName) {

    Assert.notNull(batchToSave, "BatchToSave must not be null!");
    Assert.notNull(collectionName, "CollectionName must not be null!");

    doInsertBatch(collectionName, batchToSave, this.mongoConverter);
  }

  @Override
  public void insertAll(Collection<? extends Object> objectsToSave) {

    Assert.notNull(objectsToSave, "ObjectsToSave must not be null!");
    doInsertAll(objectsToSave, this.mongoConverter);
  }

  @Override
  public void save(Object objectToSave) {

    Assert.notNull(objectToSave, "Object to save must not be null!");
    save(objectToSave, determineEntityCollectionName(objectToSave));
  }

  @Override
  public void save(Object objectToSave, String collectionName) {

    Assert.notNull(objectToSave, "Object to save must not be null!");
    Assert.hasText(collectionName, "Collection name must not be null or empty!");

    MongoPersistentEntity<?> entity = getPersistentEntity(objectToSave.getClass());

    if (entity != null && entity.hasVersionProperty()) {
      doSaveVersioned(objectToSave, entity, collectionName);
      return;
    }

    doSave(collectionName, objectToSave, this.mongoConverter);
  }

  @Override
  public UpdateResult upsert(Query query, Update update, Class<?> entityClass) {
    return doUpdate(determineCollectionName(entityClass), query, update, entityClass, true, false);
  }

  @Override
  public UpdateResult upsert(Query query, Update update, String collectionName) {
    return doUpdate(collectionName, query, update, null, true, false);
  }

  @Override
  public UpdateResult upsert(Query query, Update update, Class<?> entityClass, String collectionName) {

    Assert.notNull(entityClass, "EntityClass must not be null!");

    return doUpdate(collectionName, query, update, entityClass, true, false);
  }

  @Override
  public UpdateResult updateFirst(Query query, Update update, Class<?> entityClass) {
    return doUpdate(determineCollectionName(entityClass), query, update, entityClass, false, false);
  }

  @Override
  public UpdateResult updateFirst(final Query query, final Update update, final String collectionName) {
    return doUpdate(collectionName, query, update, null, false, false);
  }

  @Override
  public UpdateResult updateFirst(Query query, Update update, Class<?> entityClass, String collectionName) {

    Assert.notNull(entityClass, "EntityClass must not be null!");

    return doUpdate(collectionName, query, update, entityClass, false, false);
  }

  @Override
  public UpdateResult updateMulti(Query query, Update update, Class<?> entityClass) {
    return doUpdate(determineCollectionName(entityClass), query, update, entityClass, false, true);
  }

  @Override
  public UpdateResult updateMulti(final Query query, final Update update, String collectionName) {
    return doUpdate(collectionName, query, update, null, false, true);
  }

  @Override
  public UpdateResult updateMulti(final Query query, final Update update, Class<?> entityClass, String collectionName) {

    Assert.notNull(entityClass, "EntityClass must not be null!");

    return doUpdate(collectionName, query, update, entityClass, false, true);
  }

  @Override
  public DeleteResult remove(Object object) {

    Assert.notNull(object, "Object must not be null!");

    return remove(getIdQueryFor(object), object.getClass());
  }

  @Override
  public DeleteResult remove(Object object, String collectionName) {

    Assert.notNull(object, "Object must not be null!");
    Assert.hasText(collectionName, "Collection name must not be null or empty!");

    return doRemove(collectionName, getIdQueryFor(object), object.getClass());
  }

  @Override
  public DeleteResult remove(Query query, String collectionName) {
    return doRemove(collectionName, query, null);
  }

  @Override
  public DeleteResult remove(Query query, Class<?> entityClass) {
    return remove(query, entityClass, determineCollectionName(entityClass));
  }

  @Override
  public DeleteResult remove(Query query, Class<?> entityClass, String collectionName) {

    Assert.notNull(entityClass, "EntityClass must not be null!");
    return doRemove(collectionName, query, entityClass);
  }

  @Override
  public <T> List<T> findAll(Class<T> entityClass) {
    return findAll(entityClass, determineCollectionName(entityClass));
  }

  @Override
  public <T> List<T> findAll(Class<T> entityClass, String collectionName) {
    return executeFindMultiInternal(new MongoTemplate.FindCallback(new Document(), new Document()), null,
        new MongoTemplate.ReadDocumentCallback<T>(mongoConverter, entityClass, collectionName), collectionName);
  }

  @Override
  public <T> MapReduceResults<T> mapReduce(String inputCollectionName, String mapFunction, String reduceFunction,
                                           Class<T> entityClass) {
    return mapReduce(new Query(), inputCollectionName, mapFunction, reduceFunction,
        new MapReduceOptions().outputTypeInline(), entityClass);
  }

  @Override
  public <T> MapReduceResults<T> mapReduce(String inputCollectionName, String mapFunction, String reduceFunction,
                                            MapReduceOptions mapReduceOptions, Class<T> entityClass) {
    return mapReduce(new Query(), inputCollectionName, mapFunction, reduceFunction, mapReduceOptions, entityClass);
  }

  @Override
  public <T> MapReduceResults<T> mapReduce(Query query, String inputCollectionName, String mapFunction,
                                           String reduceFunction, Class<T> entityClass) {
    return mapReduce(query, inputCollectionName, mapFunction, reduceFunction, new MapReduceOptions().outputTypeInline(),
        entityClass);
  }

  @Override
  public <T> MapReduceResults<T> mapReduce(Query query, String inputCollectionName, String mapFunction,
                                           String reduceFunction,  MapReduceOptions mapReduceOptions, Class<T> entityClass) {

    Assert.notNull(query, "Query must not be null!");
    Assert.notNull(inputCollectionName, "InputCollectionName must not be null!");
    Assert.notNull(entityClass, "EntityClass must not be null!");
    Assert.notNull(reduceFunction, "ReduceFunction must not be null!");
    Assert.notNull(mapFunction, "MapFunction must not be null!");

    String mapFunc = replaceWithResourceIfNecessary(mapFunction);
    String reduceFunc = replaceWithResourceIfNecessary(reduceFunction);
    MongoCollection<Document> inputCollection = getCollection(inputCollectionName);

    // MapReduceOp
    MapReduceIterable<Document> result = inputCollection.mapReduce(mapFunc, reduceFunc);
    if (query != null && result != null) {

      if (query.getLimit() > 0 && mapReduceOptions.getLimit() == null) {
        result = result.limit(query.getLimit());
      }
      if (query.getMeta() != null && query.getMeta().getMaxTimeMsec() != null) {
        result = result.maxTime(query.getMeta().getMaxTimeMsec(), TimeUnit.MILLISECONDS);
      }
      result = result.sort(getMappedSortObject(query, entityClass));

      result = result.filter(queryMapper.getMappedObject(query.getQueryObject(), Optional.empty()));
    }

    Optional<Collation> collation = query.getCollation();

    if (mapReduceOptions != null) {

      Optionals.ifAllPresent(collation, mapReduceOptions.getCollation(), (l, r) -> {
        throw new IllegalArgumentException(
            "Both Query and MapReduceOptions define a collation. Please provide the collation only via one of the two.");
      });

      if (mapReduceOptions.getCollation().isPresent()) {
        collation = mapReduceOptions.getCollation();
      }

      if (!CollectionUtils.isEmpty(mapReduceOptions.getScopeVariables())) {
        result = result.scope(new Document(mapReduceOptions.getScopeVariables()));
      }

      if (mapReduceOptions.getLimit() != null && mapReduceOptions.getLimit().intValue() > 0) {
        result = result.limit(mapReduceOptions.getLimit());
      }

      if (mapReduceOptions.getFinalizeFunction().filter(StringUtils::hasText).isPresent()) {
        result = result.finalizeFunction(mapReduceOptions.getFinalizeFunction().get());
      }

      if (mapReduceOptions.getJavaScriptMode() != null) {
        result = result.jsMode(mapReduceOptions.getJavaScriptMode());
      }

      if (mapReduceOptions.getOutputSharded().isPresent()) {
        result = result.sharded(mapReduceOptions.getOutputSharded().get());
      }

      if (StringUtils.hasText(mapReduceOptions.getOutputCollection()) && !mapReduceOptions.usesInlineOutput()) {

        result = result.collectionName(mapReduceOptions.getOutputCollection())
            .action(mapReduceOptions.getMapReduceAction());

        if (mapReduceOptions.getOutputDatabase().isPresent()) {
          result = result.databaseName(mapReduceOptions.getOutputDatabase().get());
        }
      }
    }

    result = collation.map(Collation::toMongoCollation).map(result::collation).orElse(result);

    List<T> mappedResults = new ArrayList<T>();
    MongoTemplate.DocumentCallback<T> callback = new MongoTemplate.ReadDocumentCallback<T>(mongoConverter, entityClass, inputCollectionName);

    for (Document document : result) {
      mappedResults.add(callback.doWith(document));
    }

    return new MapReduceResults<T>(mappedResults, new Document());
  }

  @Override
  public <T> GroupByResults<T> group(String inputCollectionName, GroupBy groupBy, Class<T> entityClass) {
    return group(null, inputCollectionName, groupBy, entityClass);
  }

  @Override
  public <T> GroupByResults<T> group( Criteria criteria, String inputCollectionName, GroupBy groupBy,
                                     Class<T> entityClass) {

    Document document = groupBy.getGroupByObject();
    document.put("ns", inputCollectionName);

    if (criteria == null) {
      document.put("cond", null);
    } else {
      document.put("cond", queryMapper.getMappedObject(criteria.getCriteriaObject(), Optional.empty()));
    }
    // If initial document was a JavaScript string, potentially loaded by Spring's Resource abstraction, load it and
    // convert to Document

    if (document.containsKey("initial")) {
      Object initialObj = document.get("initial");
      if (initialObj instanceof String) {
        String initialAsString = replaceWithResourceIfNecessary((String) initialObj);
        document.put("initial", Document.parse(initialAsString));
      }
    }

    if (document.containsKey("$reduce")) {
      document.put("$reduce", replaceWithResourceIfNecessary(document.get("$reduce").toString()));
    }
    if (document.containsKey("$keyf")) {
      document.put("$keyf", replaceWithResourceIfNecessary(document.get("$keyf").toString()));
    }
    if (document.containsKey("finalize")) {
      document.put("finalize", replaceWithResourceIfNecessary(document.get("finalize").toString()));
    }

    Document commandObject = new Document("group", document);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Executing Group with Document [{}]", serializeToJsonSafely(commandObject));
    }

    Document commandResult = executeCommand(commandObject);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Group command result = [{}]", commandResult);
    }

    @SuppressWarnings("unchecked")
    Iterable<Document> resultSet = (Iterable<Document>) commandResult.get("retval");
    List<T> mappedResults = new ArrayList<T>();
    MongoTemplate.DocumentCallback<T> callback = new MongoTemplate.ReadDocumentCallback<T>(mongoConverter, entityClass, inputCollectionName);

    for (Document resultDocument : resultSet) {
      mappedResults.add(callback.doWith(resultDocument));
    }

    return new GroupByResults<T>(mappedResults, commandResult);
  }

  /* (non-Javadoc)
   * @see org.springframework.data.mongodb.core.MongoOperations#aggregate(org.springframework.data.mongodb.core.aggregation.TypedAggregation, java.lang.Class)
   */
  @Override
  public <O> AggregationResults<O> aggregate(TypedAggregation<?> aggregation, Class<O> outputType) {
    return aggregate(aggregation, determineCollectionName(aggregation.getInputType()), outputType);
  }

  /* (non-Javadoc)
   * @see org.springframework.data.mongodb.core.MongoOperations#aggregate(org.springframework.data.mongodb.core.aggregation.TypedAggregation, java.lang.String, java.lang.Class)
   */
  @Override
  public <O> AggregationResults<O> aggregate(TypedAggregation<?> aggregation, String inputCollectionName,
                                             Class<O> outputType) {

    Assert.notNull(aggregation, "Aggregation pipeline must not be null!");

    AggregationOperationContext context = new TypeBasedAggregationOperationContext(aggregation.getInputType(),
        mappingContext, queryMapper);
    return aggregate(aggregation, inputCollectionName, outputType, context);
  }

  /* (non-Javadoc)
   * @see org.springframework.data.mongodb.core.MongoOperations#aggregate(org.springframework.data.mongodb.core.aggregation.Aggregation, java.lang.Class, java.lang.Class)
   */
  @Override
  public <O> AggregationResults<O> aggregate(Aggregation aggregation, Class<?> inputType, Class<O> outputType) {

    return aggregate(aggregation, determineCollectionName(inputType), outputType,
        new TypeBasedAggregationOperationContext(inputType, mappingContext, queryMapper));
  }

  /* (non-Javadoc)
   * @see org.springframework.data.mongodb.core.MongoOperations#aggregate(org.springframework.data.mongodb.core.aggregation.Aggregation, java.lang.String, java.lang.Class)
   */
  @Override
  public <O> AggregationResults<O> aggregate(Aggregation aggregation, String collectionName, Class<O> outputType) {
    return aggregate(aggregation, collectionName, outputType, null);
  }

  /* (non-Javadoc)
   * @see org.springframework.data.mongodb.core.MongoOperations#aggregateStream(org.springframework.data.mongodb.core.aggregation.TypedAggregation, java.lang.String, java.lang.Class)
   */
  @Override
  public <O> CloseableIterator<O> aggregateStream(TypedAggregation<?> aggregation, String inputCollectionName,
                                                  Class<O> outputType) {

    Assert.notNull(aggregation, "Aggregation pipeline must not be null!");

    AggregationOperationContext context = new TypeBasedAggregationOperationContext(aggregation.getInputType(),
        mappingContext, queryMapper);
    return aggregateStream(aggregation, inputCollectionName, outputType, context);
  }

  /* (non-Javadoc)
   * @see org.springframework.data.mongodb.core.MongoOperations#aggregateStream(org.springframework.data.mongodb.core.aggregation.TypedAggregation, java.lang.Class)
   */
  @Override
  public <O> CloseableIterator<O> aggregateStream(TypedAggregation<?> aggregation, Class<O> outputType) {
    return aggregateStream(aggregation, determineCollectionName(aggregation.getInputType()), outputType);
  }

  /* (non-Javadoc)
   * @see org.springframework.data.mongodb.core.MongoOperations#aggregateStream(org.springframework.data.mongodb.core.aggregation.Aggregation, java.lang.Class, java.lang.Class)
   */
  @Override
  public <O> CloseableIterator<O> aggregateStream(Aggregation aggregation, Class<?> inputType, Class<O> outputType) {

    return aggregateStream(aggregation, determineCollectionName(inputType), outputType,
        new TypeBasedAggregationOperationContext(inputType, mappingContext, queryMapper));
  }

  /* (non-Javadoc)
   * @see org.springframework.data.mongodb.core.MongoOperations#aggregateStream(org.springframework.data.mongodb.core.aggregation.Aggregation, java.lang.String, java.lang.Class)
   */
  @Override
  public <O> CloseableIterator<O> aggregateStream(Aggregation aggregation, String collectionName, Class<O> outputType) {
    return aggregateStream(aggregation, collectionName, outputType, null);
  }

  /* (non-Javadoc)
   * @see org.springframework.data.mongodb.core.MongoOperations#findAllAndRemove(org.springframework.data.mongodb.core.query.Query, java.lang.String)
   */
  @Override
  public <T> List<T> findAllAndRemove(Query query, String collectionName) {
    return (List<T>) findAllAndRemove(query, Object.class, collectionName);
  }

  /* (non-Javadoc)
   * @see org.springframework.data.mongodb.core.MongoOperations#findAllAndRemove(org.springframework.data.mongodb.core.query.Query, java.lang.Class)
   */
  @Override
  public <T> List<T> findAllAndRemove(Query query, Class<T> entityClass) {
    return findAllAndRemove(query, entityClass, determineCollectionName(entityClass));
  }

  /* (non-Javadoc)
   * @see org.springframework.data.mongodb.core.MongoOperations#findAllAndRemove(org.springframework.data.mongodb.core.query.Query, java.lang.Class, java.lang.String)
   */
  @Override
  public <T> List<T> findAllAndRemove(Query query, Class<T> entityClass, String collectionName) {
    return doFindAndDelete(collectionName, query, entityClass);
  }
  /*
   * (non-Javadoc)
   * @see org.springframework.data.mongodb.core.ExecutableFindOperation#query(java.lang.Class)
   */
  @Override
  public <T> ExecutableFind<T> query(Class<T> domainType) {
    return new ExecutableFindOperationSupport(this).query(domainType);
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.data.mongodb.core.ExecutableUpdateOperation#update(java.lang.Class)
   */
  @Override
  public <T> ExecutableUpdate<T> update(Class<T> domainType) {
    return new ExecutableUpdateOperationSupport(this).update(domainType);
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.data.mongodb.core.ExecutableRemoveOperation#remove(java.lang.Class)
   */
  @Override
  public <T> ExecutableRemove<T> remove(Class<T> domainType) {
    return new ExecutableRemoveOperationSupport(this).remove(domainType);
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.data.mongodb.core.ExecutableAggregationOperation#aggregateAndReturn(java.lang.Class)
   */
  @Override
  public <T> ExecutableAggregation<T> aggregateAndReturn(Class<T> domainType) {
    return new ExecutableAggregationOperationSupport(this).aggregateAndReturn(domainType);
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.data.mongodb.core.ExecutableInsertOperation#insert(java.lang.Class)
   */
  @Override
  public <T> ExecutableInsert<T> insert(Class<T> domainType) {
    return new ExecutableInsertOperationSupport(this).insert(domainType);
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.data.mongodb.core.ExecutableInsertOperation#getCollectionNames()
   */
  @Override
  public Set<String> getCollectionNames() {
    return execute(new DbCallback<Set<String>>() {
      @Override
  public Set<String> doInDB(MongoDatabase db) throws MongoException, DataAccessException {
        Set<String> result = new LinkedHashSet<String>();
        for (String name : db.listCollectionNames()) {
          result.add(name);
        }
        return result;
      }
    });
  }

  @Override
  public MongoDatabase getDb() {
    return mongoDbFactory.getDb();
  }

  @Override
  public PersistenceExceptionTranslator getExceptionTranslator() {
    return exceptionTranslator;
  }


  @Override
  public MongoDbFactory getMongoDbFactory() {
    return mongoDbFactory;
  }

}
