package com.github.cloudyrock.mongock.driver.mongodb.springdata.v4.decorator.impl;

import com.mongodb.ClientSessionOptions;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.DbCallback;
import org.springframework.data.mongodb.core.DocumentCallbackHandler;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;
import org.springframework.data.mongodb.core.ExecutableFindOperation;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.SessionScoped;
import org.springframework.data.mongodb.core.WriteConcernResolver;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.*;

/**
 * From Mongock version 5, this class is deprecated and shouldn't be used (remains in code for backwards compatibility).
 *
 * Please follow one of the recommended approaches depending on your use case:
 *  - For existing changeLogs/changeSets created prior version 5: leave them untouched (use with the deprecated class)
 *  - For new changeLogs/changeSets created  from version 5: ChangeLogs/changeSets use the MongoTemplate provided by the
 *   spring framework rather than this class.
 *
 * @see MongoTemplate
 */
@Deprecated
public class MongockTemplate {


  private final MongoTemplate impl;

  public MongockTemplate(MongoTemplate impl) {
    this.impl = impl;
  }
  
  private MongoTemplate getImpl() {
    return impl;
  }

  
  public void setWriteResultChecking(WriteResultChecking resultChecking) {
    getImpl().setWriteResultChecking(resultChecking);
  }

  
  public void setWriteConcern(WriteConcern writeConcern) {
    getImpl().setWriteConcern(writeConcern);
  }

  
  public void setWriteConcernResolver(WriteConcernResolver writeConcernResolver) {
    getImpl().setWriteConcernResolver(writeConcernResolver);
  }

  
  public void setReadPreference(ReadPreference readPreference) {
    getImpl().setReadPreference(readPreference);
  }


  
  
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    getImpl().setApplicationContext(applicationContext);
  }

  
  
  public MongoConverter getConverter() {
    return getImpl().getConverter();
  }

  
  public <T> Stream<T> stream(final Query query, final Class<T> entityType) {
   return getImpl().stream(query, entityType);
  }

  
  public <T> Stream<T> stream(final Query query, final Class<T> entityType, final String collectionName) {
    return getImpl().stream(query, entityType, collectionName);
  }

  
  
  public String getCollectionName(Class<?> entityClass) {
    return getImpl().getCollectionName(entityClass);
  }

  
  public Document executeCommand(final String jsonCommand) {
    return getImpl().executeCommand(jsonCommand);
  }

  
  public Document executeCommand(final Document command) {
    return getImpl().executeCommand(command);
  }

  
  public Document executeCommand(Document command, ReadPreference readPreference) {
    return getImpl().executeCommand(command, readPreference);
  }

  
  public void executeQuery(Query query, String collectionName, DocumentCallbackHandler dch) {
    getImpl().executeQuery(query, collectionName, dch);
  }

  
  public <T> T execute(DbCallback<T> action) {
    return getImpl().execute(action);
  }

  
  public <T> T execute(Class<?> entityClass, CollectionCallback<T> callback) {
    return getImpl().execute(entityClass, callback);
  }

  
  public <T> T execute(String collectionName, CollectionCallback<T> callback) {
    return getImpl().execute(collectionName, callback);
  }

  
  public <T> MongoCollection<Document> createCollection(Class<T> entityClass) {
    return /**new MongoCollectionDecoratorImpl<>(**/getImpl().createCollection(entityClass);
  }


  
  public <T> MongoCollection<Document> createCollection(Class<T> entityClass, CollectionOptions collectionOptions) {
    return /**new MongoCollectionDecoratorImpl<>(**/getImpl().createCollection(entityClass, collectionOptions);
  }

  
  public MongoCollection<Document> createCollection(final String collectionName) {
    return /**new MongoCollectionDecoratorImpl<>(**/getImpl().createCollection(collectionName);
  }

  
  public MongoCollection<Document> createCollection(final String collectionName, final CollectionOptions collectionOptions) {
    return /**new MongoCollectionDecoratorImpl<>(**/getImpl().createCollection(collectionName, collectionOptions);
  }

  
  public MongoCollection<Document> getCollection(final String collectionName) {
    return /**new MongoCollectionDecoratorImpl<>(**/getImpl().getCollection(collectionName);
  }

  
  public <T> boolean collectionExists(Class<T> entityClass) {
    return getImpl().collectionExists(entityClass);
  }

  
  public boolean collectionExists(final String collectionName) {
    return getImpl().collectionExists(collectionName);
  }

  
  public <T> void dropCollection(Class<T> entityClass) {
    getImpl().dropCollection(entityClass);
  }

  
  public void dropCollection(String collectionName) {
    getImpl().dropCollection(collectionName);
  }

  
  public <T> T findOne(Query query, Class<T> entityClass) {
    return getImpl().findOne(query, entityClass);
  }


  
  public <T> T findOne(Query query, Class<T> entityClass, String collectionName) {
    return getImpl().findOne(query, entityClass, collectionName);
  }

  
  public boolean exists(Query query, Class<?> entityClass) {
    return getImpl().exists(query, entityClass);
  }

  
  public boolean exists(Query query, String collectionName) {
    return getImpl().exists(query, collectionName);
  }

  
  public boolean exists(Query query, Class<?> entityClass, String collectionName) {
    return getImpl().exists(query, entityClass, collectionName);
  }

  
  public <T> List<T> find(Query query, Class<T> entityClass) {
    return getImpl().find(query, entityClass);
  }

  
  public <T> List<T> find(Query query, Class<T> entityClass, String collectionName) {
    return getImpl().find(query, entityClass, collectionName);
  }

  
  public <T> T findById(Object id, Class<T> entityClass) {
    return getImpl().findById(id, entityClass);
  }


  
  public <T> T findById(Object id, Class<T> entityClass, String collectionName) {
    return getImpl().findById(id, entityClass, collectionName);
  }

  
  public <T> GeoResults<T> geoNear(NearQuery near, Class<T> entityClass) {
    return getImpl().geoNear(near, entityClass);
  }

  
  @SuppressWarnings("unchecked")
  public <T> GeoResults<T> geoNear(NearQuery near, Class<T> domainType, String collectionName) {
    return getImpl().geoNear(near, domainType, collectionName);
  }

  public <T> GeoResults<T> geoNear(NearQuery near, Class<?> domainType, String collectionName, Class<T> returnType) {
    return getImpl().geoNear(near, domainType, collectionName, returnType);
  }

  
  public <T> T findAndRemove(Query query, Class<T> entityClass) {
    return getImpl().findAndRemove(query, entityClass);
  }


  
  public <T> T findAndRemove(Query query, Class<T> entityClass, String collectionName) {
    return getImpl().findAndRemove(query, entityClass, collectionName);
  }

  
  public long count(Query query, Class<?> entityClass) {
    return getImpl().count(query, entityClass);
  }

  
  public long count(final Query query, String collectionName) {
    return getImpl().count(query, collectionName);
  }

  
  public long estimatedCount(Class<?> entityClass) {
    return getImpl().estimatedCount(entityClass);
  }

  
  public long estimatedCount(String s) {
    return getImpl().estimatedCount(s);
  }

  
  public long count(Query query, Class<?> entityClass, String collectionName) {
    return getImpl().count(query, entityClass, collectionName);
  }

  
  public <T> T insert(T objectToSave) {
    return getImpl().insert(objectToSave);
  }

  
  public <T> T insert(T objectToSave, String collectionName) {
    return getImpl().insert(objectToSave, collectionName);
  }


  
  public <T> Collection<T> insert(Collection<? extends T> batchToSave, Class<?> entityClass) {
    return getImpl().insert(batchToSave, entityClass);
  }

  
  public <T> Collection<T> insert(Collection<? extends T> batchToSave, String collectionName) {
    return getImpl().insert(batchToSave, collectionName);
  }

  
  public <T> Collection<T> insertAll(Collection<? extends T> objectsToSave) {
    return getImpl().insertAll(objectsToSave);
  }

  
  public <T> T save(T objectToSave) {
    return getImpl().save(objectToSave);
  }

  
  public <T> T save(T objectToSave, String collectionName) {
    return getImpl().save(objectToSave, collectionName);
  }

  
  public DeleteResult remove(Object object) {
    return getImpl().remove(object);
  }

  
  public DeleteResult remove(Object object, String collectionName) {
    return getImpl().remove(object, collectionName);
  }

  
  public DeleteResult remove(Query query, String collectionName) {
    return getImpl().remove(query, collectionName);
  }

  
  public DeleteResult remove(Query query, Class<?> entityClass) {
    return getImpl().remove(query, entityClass);
  }

  
  public DeleteResult remove(Query query, Class<?> entityClass, String collectionName) {
    return getImpl().remove(query, entityClass, collectionName);
  }

  
  public <T> List<T> findAll(Class<T> entityClass) {
    return getImpl().findAll(entityClass);
  }

  
  public <T> List<T> findAll(Class<T> entityClass, String collectionName) {
    return getImpl().findAll(entityClass, collectionName);
  }

  
  public <T> MapReduceResults<T> mapReduce(String inputCollectionName, String mapFunction, String reduceFunction, Class<T> entityClass) {
    return getImpl().mapReduce(inputCollectionName, mapFunction, reduceFunction, entityClass);
  }

  
  public <T> MapReduceResults<T> mapReduce(String inputCollectionName, String mapFunction, String reduceFunction, MapReduceOptions mapReduceOptions, Class<T> entityClass) {
    return getImpl().mapReduce(inputCollectionName, mapFunction, reduceFunction, mapReduceOptions, entityClass);
  }

  
  public <T> MapReduceResults<T> mapReduce(Query query, String inputCollectionName, String mapFunction, String reduceFunction, Class<T> entityClass) {
    return getImpl().mapReduce(query, inputCollectionName, mapFunction, reduceFunction, entityClass);
  }

  
  public <T> MapReduceResults<T> mapReduce(Query query, String inputCollectionName, String mapFunction, String reduceFunction, MapReduceOptions mapReduceOptions, Class<T> entityClass) {
    return getImpl().mapReduce(query, inputCollectionName, mapFunction, reduceFunction, mapReduceOptions, entityClass);
  }

  
  public <T> AggregationResults<T> group(String inputCollectionName, String groupBy, Class<T> entityClass) {
    return getImpl().aggregate(TypedAggregation.newAggregation(Aggregation.group(groupBy)), inputCollectionName, entityClass);
  }

  
  public <T> AggregationResults<T> group(Criteria criteria, String inputCollectionName, String groupBy, Class<T> entityClass) {

    return getImpl().aggregate(TypedAggregation.newAggregation(Aggregation.match(criteria), Aggregation.group(groupBy)), inputCollectionName, entityClass);
  }

  
  public <O> AggregationResults<O> aggregate(TypedAggregation<?> aggregation, Class<O> outputType) {
    return getImpl().aggregate(aggregation, outputType);
  }

  
  public <O> AggregationResults<O> aggregate(TypedAggregation<?> aggregation, String inputCollectionName, Class<O> outputType) {
    return getImpl().aggregate(aggregation, inputCollectionName, outputType);
  }

  
  public <O> AggregationResults<O> aggregate(Aggregation aggregation, Class<?> inputType, Class<O> outputType) {
    return getImpl().aggregate(aggregation, inputType, outputType);
  }

  
  public <O> AggregationResults<O> aggregate(Aggregation aggregation, String collectionName, Class<O> outputType) {
    return getImpl().aggregate(aggregation, collectionName, outputType);
  }


  
  public <O> Stream<O> aggregateStream(TypedAggregation<?> aggregation, String inputCollectionName, Class<O> outputType) {
    return getImpl().aggregateStream(aggregation, inputCollectionName, outputType);
  }

  
  public <O> Stream<O> aggregateStream(TypedAggregation<?> aggregation, Class<O> outputType) {
    return /**return new CloseableIteratorDecoratorImpl<>(**/getImpl().aggregateStream(aggregation, outputType);
  }

  
  public <O> Stream<O> aggregateStream(Aggregation aggregation, Class<?> inputType, Class<O> outputType) {
    return /**return new CloseableIteratorDecoratorImpl<>(**/getImpl().aggregateStream(aggregation, inputType, outputType);
  }

  
  public <O> Stream<O> aggregateStream(Aggregation aggregation, String collectionName, Class<O> outputType) {
    return /**return new CloseableIteratorDecoratorImpl<>(**/getImpl().aggregateStream(aggregation, collectionName, outputType);
  }


  
  public <T> List<T> findAllAndRemove(Query query, String collectionName) {
    return getImpl().findAllAndRemove(query, collectionName);
  }

  
  public <T> List<T> findAllAndRemove(Query query, Class<T> entityClass) {
    return getImpl().findAllAndRemove(query, entityClass);
  }

  
  public <T> List<T> findAllAndRemove(Query query, Class<T> entityClass, String collectionName) {
    return getImpl().findAllAndRemove(query, entityClass, collectionName);
  }

  
  public <T> ExecutableFindOperation.ExecutableFind<T> query(Class<T> domainType) {
    return /**new ExecutableFindDecoratorImpl<>(**/getImpl().query(domainType);
  }

  
  public <T> ExecutableUpdateOperation.ExecutableUpdate<T> update(Class<T> domainType) {
    return getImpl().update(domainType);
  }

  
  public <T> ExecutableRemoveOperation.ExecutableRemove<T> remove(Class<T> domainType) {
    return getImpl().remove(domainType);
  }

  
  public <T> ExecutableAggregationOperation.ExecutableAggregation<T> aggregateAndReturn(Class<T> domainType) {
    return getImpl().aggregateAndReturn(domainType);
  }

  
  public <T> ExecutableInsertOperation.ExecutableInsert<T> insert(Class<T> domainType) {
    return getImpl().insert(domainType);
  }

  
  public Set<String> getCollectionNames() {
    return getImpl().getCollectionNames();
  }

  public MongoDatabase getDb() {
    return getImpl().getDb();
  }

  
  public PersistenceExceptionTranslator getExceptionTranslator() {
    return getImpl().getExceptionTranslator();
  }

  
  public MongoDatabaseFactory getMongoDbFactory() {
    return getImpl().getMongoDatabaseFactory();
  }

  
  public <T> List<T> findDistinct(Query query, String field, Class<?> entityClass, Class<T> resultClass) {
    return getImpl().findDistinct(query, field, entityClass, resultClass);
  }

  
  public <T> List<T> findDistinct(Query query, String field, String collectionName, Class<?> entityClass, Class<T> resultClass) {
    return getImpl().findDistinct(query, field, collectionName, entityClass, resultClass);
  }

  
  public <S, T> T findAndReplace(Query query, S replacement, FindAndReplaceOptions options, Class<S> entityType, String collectionName, Class<T> resultType) {
    return getImpl().findAndReplace(query, replacement, options, entityType, collectionName, resultType);
  }

  
  public <T> ExecutableMapReduceOperation.ExecutableMapReduce<T> mapReduce(Class<T> domainType) {
    return getImpl().mapReduce(domainType);
  }

  
  public MongoOperations withSession(ClientSession session) {
    return getImpl().withSession(session);
  }

  
  public IndexOperations indexOps(String collectionName) {
    return getImpl().indexOps(collectionName);
  }

  
  public IndexOperations indexOps(String s, Class<?> aClass) {
    return getImpl().indexOps(s, aClass);
  }

  
  public IndexOperations indexOps(Class<?> entityClass) {
    return getImpl().indexOps(entityClass);
  }

  
  public BulkOperations bulkOps(BulkOperations.BulkMode mode, String collectionName) {
    return getImpl().bulkOps(mode, collectionName);
  }

  
  public BulkOperations bulkOps(BulkOperations.BulkMode mode, Class<?> entityType) {
    return  getImpl().bulkOps(mode, entityType);
  }

  
  public BulkOperations bulkOps(BulkOperations.BulkMode mode, Class<?> entityType, String collectionName) {
    return getImpl().bulkOps(mode, entityType, collectionName);
  }

  
  public ScriptOperations scriptOps() {
    return getImpl().scriptOps();
  }

  
  public SessionScoped withSession(ClientSessionOptions sessionOptions) {
    return getImpl().withSession(sessionOptions);
  }


  //default methods overwritten to ensure lock
  
  public <T> List<T> findDistinct(Query query, String field, String collection, Class<T> resultClass) {
    return getImpl().findDistinct(query, field, collection, resultClass);
  }

  
  public <T> List<T> findDistinct(String field, Class<?> entityClass, Class<T> resultClass) {
    return getImpl().findDistinct(field, entityClass, resultClass);
  }

  
  public SessionScoped withSession(Supplier<ClientSession> sessionProvider) {
    return getImpl().withSession(sessionProvider);
  }





  // since sprind-data-mongodb:3.0

  
  public <T> T findAndModify(Query query, UpdateDefinition update, Class<T> entityClass) {
    return  getImpl().findAndModify(query, update, entityClass);
  }

  
  public <T> T findAndModify(Query query, UpdateDefinition update, Class<T> entityClass, String collectionName) {
    return  getImpl().findAndModify(query, update, entityClass, collectionName);
  }

  
  public <T> T findAndModify(Query query, UpdateDefinition update, FindAndModifyOptions options, Class<T> entityClass) {
    return  getImpl().findAndModify(query, update, options, entityClass);
  }

  
  public <T> T findAndModify(Query query, UpdateDefinition update, FindAndModifyOptions options, Class<T> entityClass, String collectionName) {
    return  getImpl().findAndModify(query, update, options, entityClass, collectionName);
  }

  
  public <T> T findAndReplace(Query query, T replacement) {
    return  getImpl().findAndReplace(query, replacement);
  }

  
  public <T> T findAndReplace(Query query, T replacement, String collectionName) {
    return  getImpl().findAndReplace(query, replacement, collectionName);
  }

  
  public <T> T findAndReplace(Query query, T replacement, FindAndReplaceOptions options) {
    return  getImpl().findAndReplace(query, replacement, options);
  }

  
  public <T> T findAndReplace(Query query, T replacement, FindAndReplaceOptions options, String collectionName) {
    return  getImpl().findAndReplace(query, replacement, options, collectionName);
  }

  
  public <T> T findAndReplace(Query query, T replacement, FindAndReplaceOptions options, Class<T> entityType, String collectionName) {
    return  getImpl().findAndReplace(query, replacement, options, entityType, collectionName);
  }

  
  public  <S, T> T findAndReplace(Query query, S replacement, FindAndReplaceOptions options, Class<S> entityType, Class<T> resultType) {
    return  getImpl().findAndReplace(query, replacement, options, entityType, resultType);
  }


  
  public UpdateResult upsert(Query query, UpdateDefinition update, Class<?> entityClass) {
    return  getImpl().upsert(query, update, entityClass);
  }

  
  public UpdateResult upsert(Query query, UpdateDefinition update, String collectionName) {
    return  getImpl().upsert(query, update, collectionName);
  }

  
  public UpdateResult upsert(Query query, UpdateDefinition update, Class<?> entityClass, String collectionName) {
    return  getImpl().upsert(query, update, entityClass, collectionName);
  }

  
  public UpdateResult updateFirst(Query query, UpdateDefinition update, Class<?> entityClass) {
    return  getImpl().updateFirst(query, update, entityClass);
  }

  
  public UpdateResult updateFirst(Query query, UpdateDefinition update, String collectionName) {
    return  getImpl().updateFirst(query, update, collectionName);
  }

  
  public UpdateResult updateFirst(Query query, UpdateDefinition update, Class<?> entityClass, String collectionName) {
    return  getImpl().updateFirst(query, update, entityClass, collectionName);
  }

  
  public UpdateResult updateMulti(Query query, UpdateDefinition update, Class<?> entityClass) {
    return  getImpl().updateMulti(query, update, entityClass);
  }

  
  public UpdateResult updateMulti(Query query, UpdateDefinition update, String collectionName) {
    return  getImpl().updateMulti(query, update, collectionName);
  }

  
  public UpdateResult updateMulti(Query query, UpdateDefinition update, Class<?> entityClass, String collectionName) {
    return  getImpl().updateMulti(query, update, entityClass, collectionName);
  }
}
