package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.impl;

import io.changock.migration.api.annotations.NonLockGuarded;
import io.changock.migration.api.annotations.NonLockGuardedType;
import io.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.aggregation.impl.ExecutableAggregationDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.find.impl.ExecutableFindDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.insert.impl.ExecutableInsertDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.mapreduce.impl.ExecutableMapReduceDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.remove.impl.ExecutableRemoveDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.update.impl.ExecutableUpdateDecoratorImpl;
import io.mongock.driver.mongodb.v3.decorator.impl.MongoCollectionDecoratorImpl;
import io.mongock.driver.mongodb.v3.decorator.impl.MongoDataBaseDecoratorImpl;
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
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.DbCallback;
import org.springframework.data.mongodb.core.DocumentCallbackHandler;
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
import org.springframework.data.mongodb.core.index.IndexOperationsProvider;
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
import java.util.function.Supplier;

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
public class MongockTemplate extends DecoratorBase<MongoTemplate> implements MongoOperations, ApplicationContextAware, IndexOperationsProvider {

  public MongockTemplate(MongoTemplate impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }

  @NonLockGuarded(NonLockGuardedType.NONE)
  public void setWriteResultChecking(WriteResultChecking resultChecking) {
    getImpl().setWriteResultChecking(resultChecking);
  }

  @NonLockGuarded(NonLockGuardedType.NONE)
  public void setWriteConcern(WriteConcern writeConcern) {
    getImpl().setWriteConcern(writeConcern);
  }

  @NonLockGuarded(NonLockGuardedType.NONE)
  public void setWriteConcernResolver(WriteConcernResolver writeConcernResolver) {
    getImpl().setWriteConcernResolver(writeConcernResolver);
  }

  @NonLockGuarded(NonLockGuardedType.NONE)
  public void setReadPreference(ReadPreference readPreference) {
    getImpl().setReadPreference(readPreference);
  }


  @Override
  @NonLockGuarded(NonLockGuardedType.NONE)
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    getImpl().setApplicationContext(applicationContext);
  }

  @Override
  @NonLockGuarded(NonLockGuardedType.NONE)
  public MongoConverter getConverter() {
    return getImpl().getConverter();
  }

  @Override
  public <T> CloseableIterator<T> stream(final Query query, final Class<T> entityType) {
    return new CloseableIteratorDecoratorImpl<>(getInvoker().invoke(() -> getImpl().stream(query, entityType)), getInvoker());
  }

  @Override
  public <T> CloseableIterator<T> stream(final Query query, final Class<T> entityType, final String collectionName) {
    return new CloseableIteratorDecoratorImpl<>(getInvoker().invoke(() -> getImpl().stream(query, entityType, collectionName)), getInvoker());
  }

  @Override
  @NonLockGuarded(NonLockGuardedType.NONE)
  public String getCollectionName(Class<?> entityClass) {
    return getImpl().getCollectionName(entityClass);
  }

  @Override
  public Document executeCommand(final String jsonCommand) {
    return getInvoker().invoke(() -> getImpl().executeCommand(jsonCommand));
  }

  @Override
  public Document executeCommand(final Document command) {
    return getInvoker().invoke(() -> getImpl().executeCommand(command));
  }

  @Override
  public Document executeCommand(Document command, ReadPreference readPreference) {
    return getInvoker().invoke(() -> getImpl().executeCommand(command, readPreference));
  }

  @Override
  public void executeQuery(Query query, String collectionName, DocumentCallbackHandler dch) {
    getInvoker().invoke(() -> getImpl().executeQuery(query, collectionName, dch));
  }

  @Override
  public <T> T execute(DbCallback<T> action) {
    return getInvoker().invoke(() -> getImpl().execute(action));
  }

  @Override
  public <T> T execute(Class<?> entityClass, CollectionCallback<T> callback) {
    return getInvoker().invoke(() -> getImpl().execute(entityClass, callback));
  }

  @Override
  public <T> T execute(String collectionName, CollectionCallback<T> callback) {
    return getInvoker().invoke(() -> getImpl().execute(collectionName, callback));
  }

  @Override
  public <T> MongoCollection<Document> createCollection(Class<T> entityClass) {
    return new MongoCollectionDecoratorImpl<>(getInvoker().invoke(() -> getImpl().createCollection(entityClass)), getInvoker());
  }


  @Override
  public <T> MongoCollection<Document> createCollection(Class<T> entityClass, CollectionOptions collectionOptions) {
    return new MongoCollectionDecoratorImpl<>(getInvoker().invoke(() -> getImpl().createCollection(entityClass, collectionOptions)), getInvoker());
  }

  @Override
  public MongoCollection<Document> createCollection(final String collectionName) {
    return new MongoCollectionDecoratorImpl<>(getInvoker().invoke(() -> getImpl().createCollection(collectionName)), getInvoker());
  }

  @Override
  public MongoCollection<Document> createCollection(final String collectionName, final CollectionOptions collectionOptions) {
    return new MongoCollectionDecoratorImpl<>(getInvoker().invoke(() -> getImpl().createCollection(collectionName, collectionOptions)), getInvoker());
  }

  @Override
  public MongoCollection<Document> getCollection(final String collectionName) {
    return new MongoCollectionDecoratorImpl<>(getInvoker().invoke(() -> getImpl().getCollection(collectionName)), getInvoker());
  }

  @Override
  public <T> boolean collectionExists(Class<T> entityClass) {
    return getInvoker().invoke(() -> getImpl().collectionExists(entityClass));
  }

  @Override
  public boolean collectionExists(final String collectionName) {
    return getInvoker().invoke(() -> getImpl().collectionExists(collectionName));
  }

  @Override
  public <T> void dropCollection(Class<T> entityClass) {
    getInvoker().invoke(() -> getImpl().dropCollection(entityClass));
  }

  @Override
  public void dropCollection(String collectionName) {
    getInvoker().invoke(() -> getImpl().dropCollection(collectionName));
  }

  @Override
  public <T> T findOne(Query query, Class<T> entityClass) {
    return getInvoker().invoke(() -> getImpl().findOne(query, entityClass));
  }


  @Override
  public <T> T findOne(Query query, Class<T> entityClass, String collectionName) {
    return getInvoker().invoke(() -> getImpl().findOne(query, entityClass, collectionName));
  }

  @Override
  public boolean exists(Query query, Class<?> entityClass) {
    return getInvoker().invoke(() -> getImpl().exists(query, entityClass));
  }

  @Override
  public boolean exists(Query query, String collectionName) {
    return getInvoker().invoke(() -> getImpl().exists(query, collectionName));
  }

  @Override
  public boolean exists(Query query, Class<?> entityClass, String collectionName) {
    return getInvoker().invoke(() -> getImpl().exists(query, entityClass, collectionName));
  }

  @Override
  public <T> List<T> find(Query query, Class<T> entityClass) {
    return getInvoker().invoke(() -> getImpl().find(query, entityClass));
  }

  @Override
  public <T> List<T> find(Query query, Class<T> entityClass, String collectionName) {
    return getInvoker().invoke(() -> getImpl().find(query, entityClass, collectionName));
  }

  @Override
  public <T> T findById(Object id, Class<T> entityClass) {
    return getInvoker().invoke(() -> getImpl().findById(id, entityClass));
  }


  @Override
  public <T> T findById(Object id, Class<T> entityClass, String collectionName) {
    return getInvoker().invoke(() -> getImpl().findById(id, entityClass, collectionName));
  }

  @Override
  public <T> GeoResults<T> geoNear(NearQuery near, Class<T> entityClass) {
    return getInvoker().invoke(() -> getImpl().geoNear(near, entityClass));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> GeoResults<T> geoNear(NearQuery near, Class<T> domainType, String collectionName) {
    return getInvoker().invoke(() -> getImpl().geoNear(near, domainType, collectionName));
  }

  public <T> GeoResults<T> geoNear(NearQuery near, Class<?> domainType, String collectionName, Class<T> returnType) {
    return getInvoker().invoke(() -> getImpl().geoNear(near, domainType, collectionName, returnType));
  }

  @Override
  public <T> T findAndModify(Query query, Update update, Class<T> entityClass) {
    return getInvoker().invoke(() -> getImpl().findAndModify(query, update, entityClass));
  }

  @Override
  public <T> T findAndModify(Query query, Update update, Class<T> entityClass, String collectionName) {
    return getInvoker().invoke(() -> getImpl().findAndModify(query, update, entityClass, collectionName));
  }

  @Override
  public <T> T findAndModify(Query query, Update update, FindAndModifyOptions options, Class<T> entityClass) {
    return getInvoker().invoke(() -> getImpl().findAndModify(query, update, options, entityClass));
  }


  @Override
  public <T> T findAndModify(Query query, Update update, FindAndModifyOptions options, Class<T> entityClass, String collectionName) {
    return getInvoker().invoke(() -> getImpl().findAndModify(query, update, options, entityClass, collectionName));
  }

  @Override
  public <T> T findAndRemove(Query query, Class<T> entityClass) {
    return getInvoker().invoke(() -> getImpl().findAndRemove(query, entityClass));
  }


  @Override
  public <T> T findAndRemove(Query query, Class<T> entityClass, String collectionName) {
    return getInvoker().invoke(() -> getImpl().findAndRemove(query, entityClass, collectionName));
  }

  @Override
  public long count(Query query, Class<?> entityClass) {
    return getInvoker().invoke(() -> getImpl().count(query, entityClass));
  }

  @Override
  public long count(final Query query, String collectionName) {
    return getInvoker().invoke(() -> getImpl().count(query, collectionName));
  }

  @Override
  public long count(Query query, Class<?> entityClass, String collectionName) {
    return getInvoker().invoke(() -> getImpl().count(query, entityClass, collectionName));
  }

  @Override
  public <T> T insert(T objectToSave) {
    return getInvoker().invoke(() -> getImpl().insert(objectToSave));
  }

  @Override
  public <T> T insert(T objectToSave, String collectionName) {
    return getInvoker().invoke(() -> getImpl().insert(objectToSave, collectionName));
  }


  @Override
  public <T> Collection<T> insert(Collection<? extends T> batchToSave, Class<?> entityClass) {
    return getInvoker().invoke(() -> getImpl().insert(batchToSave, entityClass));
  }

  @Override
  public <T> Collection<T> insert(Collection<? extends T> batchToSave, String collectionName) {
    return getInvoker().invoke(() -> getImpl().insert(batchToSave, collectionName));
  }

  @Override
  public <T> Collection<T> insertAll(Collection<? extends T> objectsToSave) {
    return getInvoker().invoke(() -> getImpl().insertAll(objectsToSave));
  }

  @Override
  public <T> T save(T objectToSave) {
    return getInvoker().invoke(() -> getImpl().save(objectToSave));
  }

  @Override
  public <T> T save(T objectToSave, String collectionName) {
    return getInvoker().invoke(() -> getImpl().save(objectToSave, collectionName));
  }

  @Override
  public UpdateResult upsert(Query query, Update update, Class<?> entityClass) {
    return getInvoker().invoke(() -> getImpl().upsert(query, update, entityClass));
  }

  @Override
  public UpdateResult upsert(Query query, Update update, String collectionName) {
    return getInvoker().invoke(() -> getImpl().upsert(query, update, collectionName));
  }

  @Override
  public UpdateResult upsert(Query query, Update update, Class<?> entityClass, String collectionName) {
    return getInvoker().invoke(() -> getImpl().upsert(query, update, entityClass, collectionName));
  }

  @Override
  public UpdateResult updateFirst(Query query, Update update, Class<?> entityClass) {
    return getInvoker().invoke(() -> getImpl().updateFirst(query, update, entityClass));
  }

  @Override
  public UpdateResult updateFirst(final Query query, final Update update, final String collectionName) {
    return getInvoker().invoke(() -> getImpl().updateFirst(query, update, collectionName));
  }

  @Override
  public UpdateResult updateFirst(Query query, Update update, Class<?> entityClass, String collectionName) {
    return getInvoker().invoke(() -> getImpl().updateFirst(query, update, entityClass, collectionName));
  }

  @Override
  public UpdateResult updateMulti(Query query, Update update, Class<?> entityClass) {
    return getInvoker().invoke(() -> getImpl().updateMulti(query, update, entityClass));
  }

  @Override
  public UpdateResult updateMulti(final Query query, final Update update, String collectionName) {
    return getInvoker().invoke(() -> getImpl().updateMulti(query, update, collectionName));
  }

  @Override
  public UpdateResult updateMulti(final Query query, final Update update, Class<?> entityClass, String collectionName) {
    return getInvoker().invoke(() -> getImpl().updateMulti(query, update, entityClass, collectionName));
  }

  @Override
  public DeleteResult remove(Object object) {
    return getInvoker().invoke(() -> getImpl().remove(object));
  }

  @Override
  public DeleteResult remove(Object object, String collectionName) {
    return getInvoker().invoke(() -> getImpl().remove(object, collectionName));
  }

  @Override
  public DeleteResult remove(Query query, String collectionName) {
    return getInvoker().invoke(() -> getImpl().remove(query, collectionName));
  }

  @Override
  public DeleteResult remove(Query query, Class<?> entityClass) {
    return getInvoker().invoke(() -> getImpl().remove(query, entityClass));
  }

  @Override
  public DeleteResult remove(Query query, Class<?> entityClass, String collectionName) {
    return getInvoker().invoke(() -> getImpl().remove(query, entityClass, collectionName));
  }

  @Override
  public <T> List<T> findAll(Class<T> entityClass) {
    return getInvoker().invoke(() -> getImpl().findAll(entityClass));
  }

  @Override
  public <T> List<T> findAll(Class<T> entityClass, String collectionName) {
    return getInvoker().invoke(() -> getImpl().findAll(entityClass, collectionName));
  }

  @Override
  public <T> MapReduceResults<T> mapReduce(String inputCollectionName, String mapFunction, String reduceFunction, Class<T> entityClass) {
    return getInvoker().invoke(() -> getImpl().mapReduce(inputCollectionName, mapFunction, reduceFunction, entityClass));
  }

  @Override
  public <T> MapReduceResults<T> mapReduce(String inputCollectionName, String mapFunction, String reduceFunction, MapReduceOptions mapReduceOptions, Class<T> entityClass) {
    return getInvoker().invoke(() -> getImpl().mapReduce(inputCollectionName, mapFunction, reduceFunction, mapReduceOptions, entityClass));
  }

  @Override
  public <T> MapReduceResults<T> mapReduce(Query query, String inputCollectionName, String mapFunction, String reduceFunction, Class<T> entityClass) {
    return getInvoker().invoke(() -> getImpl().mapReduce(query, inputCollectionName, mapFunction, reduceFunction, entityClass));
  }

  @Override
  public <T> MapReduceResults<T> mapReduce(Query query, String inputCollectionName, String mapFunction, String reduceFunction, MapReduceOptions mapReduceOptions, Class<T> entityClass) {
    return getInvoker().invoke(() -> getImpl().mapReduce(query, inputCollectionName, mapFunction, reduceFunction, mapReduceOptions, entityClass));
  }

  @Override
  public <T> GroupByResults<T> group(String inputCollectionName, GroupBy groupBy, Class<T> entityClass) {
    return getInvoker().invoke(() -> getImpl().group(inputCollectionName, groupBy, entityClass));
  }

  @Override
  public <T> GroupByResults<T> group(Criteria criteria, String inputCollectionName, GroupBy groupBy, Class<T> entityClass) {
    return getInvoker().invoke(() -> getImpl().group(criteria, inputCollectionName, groupBy, entityClass));
  }

  @Override
  public <O> AggregationResults<O> aggregate(TypedAggregation<?> aggregation, Class<O> outputType) {
    return getInvoker().invoke(() -> getImpl().aggregate(aggregation, outputType));
  }

  @Override
  public <O> AggregationResults<O> aggregate(TypedAggregation<?> aggregation, String inputCollectionName, Class<O> outputType) {
    return getInvoker().invoke(() -> getImpl().aggregate(aggregation, inputCollectionName, outputType));
  }

  @Override
  public <O> AggregationResults<O> aggregate(Aggregation aggregation, Class<?> inputType, Class<O> outputType) {
    return getInvoker().invoke(() -> getImpl().aggregate(aggregation, inputType, outputType));
  }

  @Override
  public <O> AggregationResults<O> aggregate(Aggregation aggregation, String collectionName, Class<O> outputType) {
    return getInvoker().invoke(() -> getImpl().aggregate(aggregation, collectionName, outputType));
  }


  @Override
  public <O> CloseableIterator<O> aggregateStream(TypedAggregation<?> aggregation, String inputCollectionName, Class<O> outputType) {
    return new CloseableIteratorDecoratorImpl<>(getInvoker().invoke(() -> getImpl().aggregateStream(aggregation, inputCollectionName, outputType)), getInvoker());
  }

  @Override
  public <O> CloseableIterator<O> aggregateStream(TypedAggregation<?> aggregation, Class<O> outputType) {
    return new CloseableIteratorDecoratorImpl<>(getInvoker().invoke(() -> getImpl().aggregateStream(aggregation, outputType)), getInvoker());
  }

  @Override
  public <O> CloseableIterator<O> aggregateStream(Aggregation aggregation, Class<?> inputType, Class<O> outputType) {
    return new CloseableIteratorDecoratorImpl<>(getInvoker().invoke(() -> getImpl().aggregateStream(aggregation, inputType, outputType)), getInvoker());
  }

  @Override
  public <O> CloseableIterator<O> aggregateStream(Aggregation aggregation, String collectionName, Class<O> outputType) {
    return new CloseableIteratorDecoratorImpl<>(getInvoker().invoke(() -> getImpl().aggregateStream(aggregation, collectionName, outputType)), getInvoker());
  }


  @Override
  public <T> List<T> findAllAndRemove(Query query, String collectionName) {
    return getInvoker().invoke(() -> getImpl().findAllAndRemove(query, collectionName));
  }

  @Override
  public <T> List<T> findAllAndRemove(Query query, Class<T> entityClass) {
    return getInvoker().invoke(() -> getImpl().findAllAndRemove(query, entityClass));
  }

  @Override
  public <T> List<T> findAllAndRemove(Query query, Class<T> entityClass, String collectionName) {
    return getInvoker().invoke(() -> getImpl().findAllAndRemove(query, entityClass, collectionName));
  }

  @Override
  public <T> ExecutableFind<T> query(Class<T> domainType) {
    return new ExecutableFindDecoratorImpl<>(getInvoker().invoke(() -> getImpl().query(domainType)), getInvoker());
  }

  @Override
  public <T> ExecutableUpdate<T> update(Class<T> domainType) {
    return new ExecutableUpdateDecoratorImpl<>(getInvoker().invoke(() -> getImpl().update(domainType)), getInvoker());
  }

  @Override
  public <T> ExecutableRemove<T> remove(Class<T> domainType) {
    return new ExecutableRemoveDecoratorImpl<>(getInvoker().invoke(() -> getImpl().remove(domainType)), getInvoker());
  }

  @Override
  public <T> ExecutableAggregation<T> aggregateAndReturn(Class<T> domainType) {
    return new ExecutableAggregationDecoratorImpl<>(getInvoker().invoke(() -> getImpl().aggregateAndReturn(domainType)), getInvoker());
  }

  @Override
  public <T> ExecutableInsert<T> insert(Class<T> domainType) {
    return new ExecutableInsertDecoratorImpl<>(getInvoker().invoke(() -> getImpl().insert(domainType)), getInvoker());
  }

  @Override
  public Set<String> getCollectionNames() {
    return getInvoker().invoke(getImpl()::getCollectionNames);
  }

  public MongoDatabase getDb() {
    return new MongoDataBaseDecoratorImpl(getInvoker().invoke(getImpl()::getDb), getInvoker());
  }

  @NonLockGuarded(NonLockGuardedType.NONE)
  public PersistenceExceptionTranslator getExceptionTranslator() {
    return getImpl().getExceptionTranslator();
  }

  @NonLockGuarded(NonLockGuardedType.NONE)
  public MongoDbFactory getMongoDbFactory() {
    return getImpl().getMongoDbFactory();
  }

  @Override
  public <T> List<T> findDistinct(Query query, String field, Class<?> entityClass, Class<T> resultClass) {
    return getInvoker().invoke(() -> getImpl().findDistinct(query, field, entityClass, resultClass));
  }

  @Override
  public <T> List<T> findDistinct(Query query, String field, String collectionName, Class<?> entityClass, Class<T> resultClass) {
    return getInvoker().invoke(() -> getImpl().findDistinct(query, field, collectionName, entityClass, resultClass));
  }

  @Override
  public <S, T> T findAndReplace(Query query, S replacement, FindAndReplaceOptions options, Class<S> entityType, String collectionName, Class<T> resultType) {
    return getInvoker().invoke(() ->
        getImpl().findAndReplace(query, replacement, options, entityType, collectionName, resultType));
  }

  @Override
  public <T> ExecutableMapReduce<T> mapReduce(Class<T> domainType) {
    return getInvoker().invoke(() -> new ExecutableMapReduceDecoratorImpl<>(getImpl().mapReduce(domainType), getInvoker()));
  }

  @Override
  public MongoOperations withSession(ClientSession session) {
    return getInvoker().invoke(() -> new MongockTemplate(getImpl().withSession(session), getInvoker()));
  }

  @Override
  public IndexOperations indexOps(String collectionName) {
    return getInvoker().invoke(() -> new IndexOperationsDecoratorImpl(getImpl().indexOps(collectionName), getInvoker()));
  }

  @Override
  public IndexOperations indexOps(Class<?> entityClass) {
    return getInvoker().invoke(() -> new IndexOperationsDecoratorImpl(getImpl().indexOps(entityClass), getInvoker()));
  }

  @Override
  public BulkOperations bulkOps(BulkOperations.BulkMode mode, String collectionName) {
    return new BulkOperationsDecoratorImpl(getInvoker().invoke(() -> getImpl().bulkOps(mode, collectionName)), getInvoker());
  }

  @Override
  public BulkOperations bulkOps(BulkOperations.BulkMode mode, Class<?> entityType) {
    return new BulkOperationsDecoratorImpl(getInvoker().invoke(() -> getImpl().bulkOps(mode, entityType)), getInvoker());
  }

  @Override
  public BulkOperations bulkOps(BulkOperations.BulkMode mode, Class<?> entityType, String collectionName) {
    return new BulkOperationsDecoratorImpl(getInvoker().invoke(() -> getImpl().bulkOps(mode, entityType, collectionName)), getInvoker());
  }

  @Override
  public ScriptOperations scriptOps() {
    return new ScriptOperationsDecoratorImpl(getInvoker().invoke(getImpl()::scriptOps), getInvoker());
  }

  @Override
  public SessionScoped withSession(ClientSessionOptions sessionOptions) {
    return getInvoker().invoke(() -> new SessionScopedDecoratorImpl(getImpl().withSession(sessionOptions), getInvoker()));
  }


  //default methods overwritten to ensure lock
  @Override
  public <T> List<T> findDistinct(Query query, String field, String collection, Class<T> resultClass) {
    return getInvoker().invoke(() -> getImpl().findDistinct(query, field, collection, resultClass));
  }

  @Override
  public <T> List<T> findDistinct(String field, Class<?> entityClass, Class<T> resultClass) {
    return getInvoker().invoke(() -> getImpl().findDistinct(field, entityClass, resultClass));
  }

  @Override
  public SessionScoped withSession(Supplier<ClientSession> sessionProvider) {
    return new SessionScopedDecoratorImpl(getInvoker().invoke(() -> getImpl().withSession(sessionProvider)), getInvoker());
  }

  @Override
  public <T> T findAndReplace(Query query, T replacement) {
    return getInvoker().invoke(() -> getImpl().findAndReplace(query, replacement));
  }

  @Override
  public <T> T findAndReplace(Query query, T replacement, String collectionName) {
    return getInvoker().invoke(() -> getImpl().findAndReplace(query, replacement, collectionName));
  }

  @Override
  public <T> T findAndReplace(Query query, T replacement, FindAndReplaceOptions options) {
    return getInvoker().invoke(() -> getImpl().findAndReplace(query, replacement, options));
  }

  @Override
  public <T> T findAndReplace(Query query, T replacement, FindAndReplaceOptions options, String collectionName) {
    return getInvoker().invoke(() -> getImpl().findAndReplace(query, replacement, options, collectionName));
  }

  @Override
  public <T> T findAndReplace(Query query, T replacement, FindAndReplaceOptions options, Class<T> entityType, String collectionName) {
    return getInvoker().invoke(() -> getImpl().findAndReplace(query, replacement, options, entityType, collectionName));
  }

  @Override
  public <S, T> T findAndReplace(Query query, S replacement, FindAndReplaceOptions options, Class<S> entityType, Class<T> resultType) {
    return getInvoker().invoke(() -> getImpl().findAndReplace(query, replacement, options, entityType, resultType));
  }


}
