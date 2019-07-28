package com.github.cloudyrock.mongock.decorator.impl;

import com.mongodb.MongoClient;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.DbCallback;
import org.springframework.data.mongodb.core.DocumentCallbackHandler;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.WriteConcernResolver;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.CloseableIterator;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class MongoTemplateDecoratorImpl extends MongoTemplate {
  private final com.github.cloudyrock.mongock.decorator.util.MethodInvoker methodInvoker;

  public MongoTemplateDecoratorImpl(MongoClient mongoClient, String databaseName, com.github.cloudyrock.mongock.decorator.util.MethodInvoker methodInvoker) {
    this(new MongoDbFactoryDecoratorImpl(new SimpleMongoDbFactory(mongoClient, databaseName), methodInvoker), methodInvoker);
  }

  public MongoTemplateDecoratorImpl(MongoDbFactory mongoDbFactory, com.github.cloudyrock.mongock.decorator.util.MethodInvoker methodInvoker) {
    this(new MongoDbFactoryDecoratorImpl(mongoDbFactory, methodInvoker), null, methodInvoker);
  }

  public MongoTemplateDecoratorImpl(MongoDbFactory mongoDbFactory, MongoConverter mongoConverter, com.github.cloudyrock.mongock.decorator.util.MethodInvoker methodInvoker) {
    super(new MongoDbFactoryDecoratorImpl(mongoDbFactory, methodInvoker), mongoConverter);
    this.methodInvoker = methodInvoker;
  }

  private com.github.cloudyrock.mongock.decorator.util.MethodInvoker getInvoker() {
    return methodInvoker;
  }


  @Override
  public void setWriteResultChecking(WriteResultChecking resultChecking) {
    super.setWriteResultChecking(resultChecking);
  }

  @Override
  public void setWriteConcern(WriteConcern writeConcern) {
    super.setWriteConcern(writeConcern);
  }


  @Override
  public void setWriteConcernResolver(WriteConcernResolver writeConcernResolver) {
    super.setWriteConcernResolver(writeConcernResolver);
  }


  @Override
  public void setReadPreference(ReadPreference readPreference) {
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
    return getInvoker().invoke(() -> super.executeCommand(jsonCommand));
  }

  @Override
  public Document executeCommand(final Document command) {
    return getInvoker().invoke(() -> super.executeCommand(command));
  }

  @Override
  public Document executeCommand(Document command, ReadPreference readPreference) {
    return getInvoker().invoke(() -> super.executeCommand(command, readPreference));
  }

  @Override
  public void executeQuery(Query query, String collectionName, DocumentCallbackHandler dch) {
    getInvoker().invoke(() -> super.executeQuery(query, collectionName, dch));
  }

  @Override
  public <T> T execute(DbCallback<T> action) {
    return getInvoker().invoke(() -> super.execute(action));
  }

  @Override
  public <T> T execute(Class<?> entityClass, CollectionCallback<T> callback) {
    return getInvoker().invoke(() -> super.execute(entityClass, callback));
  }

  @Override
  public <T> T execute(String collectionName, CollectionCallback<T> callback) {
    return getInvoker().invoke(() -> super.execute(collectionName, callback));
  }

  @Override
  public <T> MongoCollection<Document> createCollection(Class<T> entityClass) {
    return new MongoCollectionDecoratorImpl<>(getInvoker().invoke(() -> super.createCollection(entityClass)), methodInvoker);
  }


  @Override
  public <T> MongoCollection<Document> createCollection(Class<T> entityClass, CollectionOptions collectionOptions) {
    return new MongoCollectionDecoratorImpl<>(getInvoker().invoke(() -> super.createCollection(entityClass, collectionOptions)), methodInvoker);
  }

  @Override
  public MongoCollection<Document> createCollection(final String collectionName) {
    return new MongoCollectionDecoratorImpl<>(getInvoker().invoke(() -> super.createCollection(collectionName)), methodInvoker);
  }

  @Override
  public MongoCollection<Document> createCollection(final String collectionName, final CollectionOptions collectionOptions) {
    return new MongoCollectionDecoratorImpl<>(getInvoker().invoke(() -> super.createCollection(collectionName, collectionOptions)), methodInvoker);
  }

  @Override
  public MongoCollection<Document> getCollection(final String collectionName) {
    return new MongoCollectionDecoratorImpl<>(getInvoker().invoke(() -> super.getCollection(collectionName)), methodInvoker);
  }

  @Override
  public <T> boolean collectionExists(Class<T> entityClass) {
    return getInvoker().invoke(() -> super.collectionExists(entityClass));
  }

  @Override
  public boolean collectionExists(final String collectionName) {
    return getInvoker().invoke(() -> super.collectionExists(collectionName));
  }

  @Override
  public <T> void dropCollection(Class<T> entityClass) {
    getInvoker().invoke(() -> super.dropCollection(entityClass));
  }

  @Override
  public void dropCollection(String collectionName) {
    getInvoker().invoke(() -> super.dropCollection(collectionName));
  }

  @Override
  public <T> T findOne(Query query, Class<T> entityClass) {
    return getInvoker().invoke(() -> super.findOne(query, entityClass));
  }


  @Override
  public <T> T findOne(Query query, Class<T> entityClass, String collectionName) {
    return getInvoker().invoke(() -> super.findOne(query, entityClass, collectionName));
  }

  @Override
  public boolean exists(Query query, Class<?> entityClass) {
    return getInvoker().invoke(() -> super.exists(query, entityClass));
  }

  @Override
  public boolean exists(Query query, String collectionName) {
    return getInvoker().invoke(() -> super.exists(query, collectionName));
  }

  @Override
  public boolean exists(Query query, Class<?> entityClass, String collectionName) {
    return getInvoker().invoke(() -> super.exists(query, entityClass, collectionName));
  }

  @Override
  public <T> List<T> find(Query query, Class<T> entityClass) {
    return getInvoker().invoke(() -> super.find(query, entityClass));
  }

  @Override
  public <T> List<T> find(Query query, Class<T> entityClass, String collectionName) {
    return getInvoker().invoke(() -> super.find(query, entityClass, collectionName));
  }

  @Override
  public <T> T findById(Object id, Class<T> entityClass) {
    return getInvoker().invoke(() -> super.findById(id, entityClass));
  }


  @Override
  public <T> T findById(Object id, Class<T> entityClass, String collectionName) {
    return getInvoker().invoke(() -> super.findById(id, entityClass, collectionName));
  }

  @Override
  public <T> GeoResults<T> geoNear(NearQuery near, Class<T> entityClass) {
    return getInvoker().invoke(() -> super.geoNear(near, entityClass));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> GeoResults<T> geoNear(NearQuery near, Class<T> domainType, String collectionName) {
    return getInvoker().invoke(() -> super.geoNear(near, domainType, collectionName));
  }

  @Override
  public <T> GeoResults<T> geoNear(NearQuery near, Class<?> domainType, String collectionName, Class<T> returnType) {
    return getInvoker().invoke(() -> super.geoNear(near, domainType, collectionName, returnType));
  }


  @Override
  public <T> T findAndModify(Query query, Update update, Class<T> entityClass) {
    return getInvoker().invoke(() -> super.findAndModify(query, update, entityClass));
  }


  @Override
  public <T> T findAndModify(Query query, Update update, Class<T> entityClass, String collectionName) {
    return getInvoker().invoke(() -> super.findAndModify(query, update, entityClass, collectionName));
  }


  @Override
  public <T> T findAndModify(Query query, Update update, FindAndModifyOptions options, Class<T> entityClass) {
    return getInvoker().invoke(() -> super.findAndModify(query, update, options, entityClass));
  }


  @Override
  public <T> T findAndModify(Query query, Update update, FindAndModifyOptions options, Class<T> entityClass, String collectionName) {
    return getInvoker().invoke(() -> super.findAndModify(query, update, options, entityClass, collectionName));
  }

  @Override
  public <T> T findAndRemove(Query query, Class<T> entityClass) {
    return getInvoker().invoke(() -> super.findAndRemove(query, entityClass));
  }


  @Override
  public <T> T findAndRemove(Query query, Class<T> entityClass, String collectionName) {
    return getInvoker().invoke(() -> super.findAndRemove(query, entityClass, collectionName));
  }

  @Override
  public long count(Query query, Class<?> entityClass) {
    return getInvoker().invoke(() -> super.count(query, entityClass));
  }

  @Override
  public long count(final Query query, String collectionName) {
    return getInvoker().invoke(() -> super.count(query, collectionName));
  }

  @Override
  public long count(Query query, Class<?> entityClass, String collectionName) {
    return getInvoker().invoke(() -> super.count(query, entityClass, collectionName));
  }

  @Override
  public void insert(Object objectToSave) {
    getInvoker().invoke(() -> super.insert(objectToSave));
  }

  @Override
  public void insert(Object objectToSave, String collectionName) {
    getInvoker().invoke(() -> super.insert(objectToSave, collectionName));
  }


  @Override
  public void insert(Collection<? extends Object> batchToSave, Class<?> entityClass) {
    getInvoker().invoke(() -> super.insert(batchToSave, entityClass));
  }

  @Override
  public void insert(Collection<? extends Object> batchToSave, String collectionName) {
    getInvoker().invoke(() -> super.insert(batchToSave, collectionName));
  }

  @Override
  public void insertAll(Collection<? extends Object> objectsToSave) {
    getInvoker().invoke(() -> super.insertAll(objectsToSave));
  }

  @Override
  public void save(Object objectToSave) {
    getInvoker().invoke(() -> super.save(objectToSave));
  }

  @Override
  public void save(Object objectToSave, String collectionName) {
    getInvoker().invoke(() -> super.save(objectToSave, collectionName));
  }

  @Override
  public UpdateResult upsert(Query query, Update update, Class<?> entityClass) {
    return getInvoker().invoke(() -> super.upsert(query, update, entityClass));
  }

  @Override
  public UpdateResult upsert(Query query, Update update, String collectionName) {
    return getInvoker().invoke(() -> super.upsert(query, update, collectionName));
  }

  @Override
  public UpdateResult upsert(Query query, Update update, Class<?> entityClass, String collectionName) {
    return getInvoker().invoke(() -> super.upsert(query, update, entityClass, collectionName));
  }

  @Override
  public UpdateResult updateFirst(Query query, Update update, Class<?> entityClass) {
    return getInvoker().invoke(() -> super.updateFirst(query, update, entityClass));
  }

  @Override
  public UpdateResult updateFirst(final Query query, final Update update, final String collectionName) {
    return getInvoker().invoke(() -> super.upsert(query, update, collectionName));
  }

  @Override
  public UpdateResult updateFirst(Query query, Update update, Class<?> entityClass, String collectionName) {
    return getInvoker().invoke(() -> super.updateFirst(query, update, entityClass, collectionName));
  }

  @Override
  public UpdateResult updateMulti(Query query, Update update, Class<?> entityClass) {
    return getInvoker().invoke(() -> super.updateMulti(query, update, entityClass));
  }

  @Override
  public UpdateResult updateMulti(final Query query, final Update update, String collectionName) {
    return getInvoker().invoke(() -> super.updateMulti(query, update, collectionName));
  }

  @Override
  public UpdateResult updateMulti(final Query query, final Update update, Class<?> entityClass, String collectionName) {
    return getInvoker().invoke(() -> super.updateMulti(query, update, entityClass, collectionName));
  }

  @Override
  public DeleteResult remove(Object object) {
    return getInvoker().invoke(() -> super.remove(object));
  }

  @Override
  public DeleteResult remove(Object object, String collectionName) {
    return getInvoker().invoke(() -> super.remove(object, collectionName));
  }

  @Override
  public DeleteResult remove(Query query, String collectionName) {
    return getInvoker().invoke(() -> super.remove(query, collectionName));
  }

  @Override
  public DeleteResult remove(Query query, Class<?> entityClass) {
    return getInvoker().invoke(() -> super.remove(query, entityClass));
  }

  @Override
  public DeleteResult remove(Query query, Class<?> entityClass, String collectionName) {
    return getInvoker().invoke(() -> super.remove(query, entityClass, collectionName));
  }

  @Override
  public <T> List<T> findAll(Class<T> entityClass) {
    return getInvoker().invoke(() -> super.findAll(entityClass));
  }

  @Override
  public <T> List<T> findAll(Class<T> entityClass, String collectionName) {
    return getInvoker().invoke(() -> super.findAll(entityClass, collectionName));
  }

  @Override
  public <T> MapReduceResults<T> mapReduce(String inputCollectionName, String mapFunction, String reduceFunction, Class<T> entityClass) {
    return getInvoker().invoke(() -> super.mapReduce(inputCollectionName, mapFunction, reduceFunction, entityClass));
  }

  @Override
  public <T> MapReduceResults<T> mapReduce(String inputCollectionName, String mapFunction, String reduceFunction, MapReduceOptions mapReduceOptions, Class<T> entityClass) {
    return getInvoker().invoke(() -> super.mapReduce(inputCollectionName, mapFunction, reduceFunction, mapReduceOptions, entityClass));
  }

  @Override
  public <T> MapReduceResults<T> mapReduce(Query query, String inputCollectionName, String mapFunction, String reduceFunction, Class<T> entityClass) {
    return getInvoker().invoke(() -> super.mapReduce(query, inputCollectionName, mapFunction, reduceFunction, entityClass));
  }

  @Override
  public <T> MapReduceResults<T> mapReduce(Query query, String inputCollectionName, String mapFunction, String reduceFunction, MapReduceOptions mapReduceOptions, Class<T> entityClass) {
    return getInvoker().invoke(() -> super.mapReduce(query, inputCollectionName, mapFunction, reduceFunction, mapReduceOptions, entityClass));
  }

  @Override
  public <T> GroupByResults<T> group(String inputCollectionName, GroupBy groupBy, Class<T> entityClass) {
    return getInvoker().invoke(() -> super.group(inputCollectionName, groupBy, entityClass));
  }

  @Override
  public <T> GroupByResults<T> group(Criteria criteria, String inputCollectionName, GroupBy groupBy, Class<T> entityClass) {
    return getInvoker().invoke(() -> super.group(criteria, inputCollectionName, groupBy, entityClass));
  }

  @Override
  public <O> AggregationResults<O> aggregate(TypedAggregation<?> aggregation, Class<O> outputType) {
    return getInvoker().invoke(() -> super.aggregate(aggregation, outputType));
  }

  @Override
  public <O> AggregationResults<O> aggregate(TypedAggregation<?> aggregation, String inputCollectionName, Class<O> outputType) {
    return getInvoker().invoke(() -> super.aggregate(aggregation, inputCollectionName, outputType));
  }

  @Override
  public <O> AggregationResults<O> aggregate(Aggregation aggregation, Class<?> inputType, Class<O> outputType) {
    return getInvoker().invoke(() -> super.aggregate(aggregation, inputType, outputType));
  }

  @Override
  public <O> AggregationResults<O> aggregate(Aggregation aggregation, String collectionName, Class<O> outputType) {
    return getInvoker().invoke(() -> super.aggregate(aggregation, collectionName, outputType));
  }


  @Override
  public <O> CloseableIterator<O> aggregateStream(TypedAggregation<?> aggregation, String inputCollectionName, Class<O> outputType) {
    return new CloseableIteratorDecoratorImpl<>(getInvoker().invoke(() -> super.aggregateStream(aggregation, inputCollectionName, outputType)), methodInvoker);
  }

  @Override
  public <O> CloseableIterator<O> aggregateStream(TypedAggregation<?> aggregation, Class<O> outputType) {
    return new CloseableIteratorDecoratorImpl<>(getInvoker().invoke(() -> super.aggregateStream(aggregation, outputType)), methodInvoker);
  }

  @Override
  public <O> CloseableIterator<O> aggregateStream(Aggregation aggregation, Class<?> inputType, Class<O> outputType) {
    return new CloseableIteratorDecoratorImpl<>(getInvoker().invoke(() -> super.aggregateStream(aggregation, inputType, outputType)), methodInvoker);
  }

  @Override
  public <O> CloseableIterator<O> aggregateStream(Aggregation aggregation, String collectionName, Class<O> outputType) {
    return new CloseableIteratorDecoratorImpl<>(getInvoker().invoke(() -> super.aggregateStream(aggregation, collectionName, outputType)), methodInvoker);
  }


  @Override
  public <T> List<T> findAllAndRemove(Query query, String collectionName) {
    return getInvoker().invoke(() -> super.findAllAndRemove(query, collectionName));
  }

  @Override
  public <T> List<T> findAllAndRemove(Query query, Class<T> entityClass) {
    return getInvoker().invoke(() -> super.findAllAndRemove(query, entityClass));
  }

  @Override
  public <T> List<T> findAllAndRemove(Query query, Class<T> entityClass, String collectionName) {
    return getInvoker().invoke(() -> super.findAllAndRemove(query, entityClass, collectionName));
  }

  @Override
  //This relies on passing the monitored instance of mongotemplate(this) to the ExecutableFind
  public <T> ExecutableFind<T> query(Class<T> domainType) {
    return getInvoker().invoke(() -> super.query(domainType));
  }


  @Override
  //This relies on passing the monitored instance of mongotemplate(this) to the ExecutableUpdate
  public <T> ExecutableUpdate<T> update(Class<T> domainType) {
    return getInvoker().invoke(() -> super.update(domainType));
  }


  @Override
  //This relies on passing the monitored instance of mongotemplate(this) to the ExecutableRemove
  public <T> ExecutableRemove<T> remove(Class<T> domainType) {
    return getInvoker().invoke(() -> super.remove(domainType));
  }


  @Override
  //This relies on passing the monitored instance of mongotemplate(this) to the ExecutableAggregation
  public <T> ExecutableAggregation<T> aggregateAndReturn(Class<T> domainType) {
    return getInvoker().invoke(() -> super.aggregateAndReturn(domainType));
  }


  @Override
  //This relies on passing the monitored instance of mongotemplate(this) to the ExecutableInsert
  public <T> ExecutableInsert<T> insert(Class<T> domainType) {
    return getInvoker().invoke(() -> super.insert(domainType));
  }


  @Override
  public Set<String> getCollectionNames() {
    return getInvoker().invoke(() -> super.getCollectionNames());
  }

  @Override
  public MongoDatabase getDb() {
    return new MongoDataBaseDecoratorImpl(getInvoker().invoke(() -> super.getDb()), methodInvoker);
  }

  @Override
  public PersistenceExceptionTranslator getExceptionTranslator() {
    return super.getExceptionTranslator();
  }


  @Override
  public MongoDbFactory getMongoDbFactory() {
    return super.getMongoDbFactory();
  }


}
