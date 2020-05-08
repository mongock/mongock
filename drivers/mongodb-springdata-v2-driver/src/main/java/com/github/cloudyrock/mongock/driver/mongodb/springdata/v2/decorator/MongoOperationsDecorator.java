package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator;

import com.github.cloudyrock.mongock.driver.mongodb.v3.decorator.impl.MongoCollectionDecoratorImpl;
import com.mongodb.ClientSessionOptions;
import com.mongodb.ReadPreference;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.impl.BulkOperationsDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.impl.CloseableIteratorDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.impl.IndexOperationsDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.impl.MongoOperationsDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.impl.ScriptOperationsDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.impl.SessionScopedDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.aggregation.impl.ExecutableAggregationDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.find.impl.ExecutableFindDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.insert.impl.ExecutableInsertDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.mapreduce.impl.MapReduceWithMapFunctionDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.remove.impl.ExecutableRemoveDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.operation.executable.update.impl.ExecutableUpdateDecoratorImpl;
import org.bson.Document;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.DbCallback;
import org.springframework.data.mongodb.core.DocumentCallbackHandler;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.SessionScoped;
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
import java.util.function.Supplier;

public interface MongoOperationsDecorator extends MongoOperations {

    MongoOperations getImpl();

    LockGuardInvoker getInvoker();


    @Override
    default MongoConverter getConverter() {
        return getImpl().getConverter();
    }

    @Override
    default <T> CloseableIterator<T> stream(final Query query, final Class<T> entityType) {
        return getInvoker().invoke(() -> getImpl().stream(query, entityType));
    }

    @Override
    default <T> CloseableIterator<T> stream(final Query query, final Class<T> entityType, final String collectionName) {
        return getInvoker().invoke(() -> getImpl().stream(query, entityType, collectionName));
    }

    @Override
    default String getCollectionName(Class<?> entityClass) {
        return getImpl().getCollectionName(entityClass);
    }

    @Override
    default Document executeCommand(final String jsonCommand) {
        return getInvoker().invoke(() -> getImpl().executeCommand(jsonCommand));
    }

    @Override
    default Document executeCommand(final Document command) {
        return getInvoker().invoke(() -> getImpl().executeCommand(command));
    }

    @Override
    default Document executeCommand(Document command, ReadPreference readPreference) {
        return getInvoker().invoke(() -> getImpl().executeCommand(command, readPreference));
    }

    @Override
    default void executeQuery(Query query, String collectionName, DocumentCallbackHandler dch) {
        getInvoker().invoke(() -> getImpl().executeQuery(query, collectionName, dch));
    }

    @Override
    default <T> T execute(DbCallback<T> action) {
        return getInvoker().invoke(() -> getImpl().execute(action));
    }

    @Override
    default <T> T execute(Class<?> entityClass, CollectionCallback<T> callback) {
        return getInvoker().invoke(() -> getImpl().execute(entityClass, callback));
    }

    @Override
    default <T> T execute(String collectionName, CollectionCallback<T> callback) {
        return getInvoker().invoke(() -> getImpl().execute(collectionName, callback));
    }

    @Override
    default <T> MongoCollection<Document> createCollection(Class<T> entityClass) {
        return new MongoCollectionDecoratorImpl<>(getInvoker().invoke(() -> getImpl().createCollection(entityClass)), getInvoker());
    }


    @Override
    default <T> MongoCollection<Document> createCollection(Class<T> entityClass, CollectionOptions collectionOptions) {
        return new MongoCollectionDecoratorImpl<>(getInvoker().invoke(() -> getImpl().createCollection(entityClass, collectionOptions)), getInvoker());
    }

    @Override
    default MongoCollection<Document> createCollection(final String collectionName) {
        return new MongoCollectionDecoratorImpl<>(getInvoker().invoke(() -> getImpl().createCollection(collectionName)), getInvoker());
    }

    @Override
    default MongoCollection<Document> createCollection(final String collectionName, final CollectionOptions collectionOptions) {
        return new MongoCollectionDecoratorImpl<>(getInvoker().invoke(() -> getImpl().createCollection(collectionName, collectionOptions)), getInvoker());
    }

    @Override
    default MongoCollection<Document> getCollection(final String collectionName) {
        return new MongoCollectionDecoratorImpl<>(getInvoker().invoke(() -> getImpl().getCollection(collectionName)), getInvoker());
    }

    @Override
    default <T> boolean collectionExists(Class<T> entityClass) {
        return getInvoker().invoke(() -> getImpl().collectionExists(entityClass));
    }

    @Override
    default boolean collectionExists(final String collectionName) {
        return getInvoker().invoke(() -> getImpl().collectionExists(collectionName));
    }

    @Override
    default <T> void dropCollection(Class<T> entityClass) {
        getInvoker().invoke(() -> getImpl().dropCollection(entityClass));
    }

    @Override
    default void dropCollection(String collectionName) {
        getInvoker().invoke(() -> getImpl().dropCollection(collectionName));
    }

    @Override
    default <T> T findOne(Query query, Class<T> entityClass) {
        return getInvoker().invoke(() -> getImpl().findOne(query, entityClass));
    }


    @Override
    default <T> T findOne(Query query, Class<T> entityClass, String collectionName) {
        return getInvoker().invoke(() -> getImpl().findOne(query, entityClass, collectionName));
    }

    @Override
    default boolean exists(Query query, Class<?> entityClass) {
        return getInvoker().invoke(() -> getImpl().exists(query, entityClass));
    }

    @Override
    default boolean exists(Query query, String collectionName) {
        return getInvoker().invoke(() -> getImpl().exists(query, collectionName));
    }

    @Override
    default boolean exists(Query query, Class<?> entityClass, String collectionName) {
        return getInvoker().invoke(() -> getImpl().exists(query, entityClass, collectionName));
    }

    @Override
    default <T> List<T> find(Query query, Class<T> entityClass) {
        return getInvoker().invoke(() -> getImpl().find(query, entityClass));
    }

    @Override
    default <T> List<T> find(Query query, Class<T> entityClass, String collectionName) {
        return getInvoker().invoke(() -> getImpl().find(query, entityClass, collectionName));
    }

    @Override
    default <T> T findById(Object id, Class<T> entityClass) {
        return getInvoker().invoke(() -> getImpl().findById(id, entityClass));
    }


    @Override
    default <T> T findById(Object id, Class<T> entityClass, String collectionName) {
        return getInvoker().invoke(() -> getImpl().findById(id, entityClass, collectionName));
    }

    @Override
    default <T> GeoResults<T> geoNear(NearQuery near, Class<T> entityClass) {
        return getInvoker().invoke(() -> getImpl().geoNear(near, entityClass));
    }

    @Override
    @SuppressWarnings("unchecked")
    default <T> GeoResults<T> geoNear(NearQuery near, Class<T> domainType, String collectionName) {
        return getInvoker().invoke(() -> getImpl().geoNear(near, domainType, collectionName));
    }


    @Override
    default <T> T findAndModify(Query query, Update update, Class<T> entityClass) {
        return getInvoker().invoke(() -> getImpl().findAndModify(query, update, entityClass));
    }


    @Override
    default <T> T findAndModify(Query query, Update update, Class<T> entityClass, String collectionName) {
        return getInvoker().invoke(() -> getImpl().findAndModify(query, update, entityClass, collectionName));
    }


    @Override
    default <T> T findAndModify(Query query, Update update, FindAndModifyOptions options, Class<T> entityClass) {
        return getInvoker().invoke(() -> getImpl().findAndModify(query, update, options, entityClass));
    }


    @Override
    default <T> T findAndModify(Query query, Update update, FindAndModifyOptions options, Class<T> entityClass, String collectionName) {
        return getInvoker().invoke(() -> getImpl().findAndModify(query, update, options, entityClass, collectionName));
    }

    @Override
    default <T> T findAndRemove(Query query, Class<T> entityClass) {
        return getInvoker().invoke(() -> getImpl().findAndRemove(query, entityClass));
    }


    @Override
    default <T> T findAndRemove(Query query, Class<T> entityClass, String collectionName) {
        return getInvoker().invoke(() -> getImpl().findAndRemove(query, entityClass, collectionName));
    }

    @Override
    default long count(Query query, Class<?> entityClass) {
        return getInvoker().invoke(() -> getImpl().count(query, entityClass));
    }

    @Override
    default long count(final Query query, String collectionName) {
        return getInvoker().invoke(() -> getImpl().count(query, collectionName));
    }

    @Override
    default long count(Query query, Class<?> entityClass, String collectionName) {
        return getInvoker().invoke(() -> getImpl().count(query, entityClass, collectionName));
    }

    @Override
    default <T> T insert(T objectToSave) {
        return getInvoker().invoke(() -> getImpl().insert(objectToSave));
    }

    @Override
    default <T> T insert(T objectToSave, String collectionName) {
        return getInvoker().invoke(() -> getImpl().insert(objectToSave, collectionName));
    }


    @Override
    default <T> Collection<T> insert(Collection<? extends T> batchToSave, Class<?> entityClass) {
        return getInvoker().invoke(() -> getImpl().insert(batchToSave, entityClass));
    }

    @Override
    default <T> Collection<T> insert(Collection<? extends T> batchToSave, String collectionName) {
        return getInvoker().invoke(() -> getImpl().insert(batchToSave, collectionName));
    }

    @Override
    default <T> Collection<T> insertAll(Collection<? extends T> objectsToSave) {
        return getInvoker().invoke(() -> getImpl().insertAll(objectsToSave));
    }

    @Override
    default <T> T save(T objectToSave) {
        return getInvoker().invoke(() -> getImpl().save(objectToSave));
    }

    @Override
    default <T> T save(T objectToSave, String collectionName) {
        return getInvoker().invoke(() -> getImpl().save(objectToSave, collectionName));
    }

    @Override
    default UpdateResult upsert(Query query, Update update, Class<?> entityClass) {
        return getInvoker().invoke(() -> getImpl().upsert(query, update, entityClass));
    }

    @Override
    default UpdateResult upsert(Query query, Update update, String collectionName) {
        return getInvoker().invoke(() -> getImpl().upsert(query, update, collectionName));
    }

    @Override
    default UpdateResult upsert(Query query, Update update, Class<?> entityClass, String collectionName) {
        return getInvoker().invoke(() -> getImpl().upsert(query, update, entityClass, collectionName));
    }

    @Override
    default UpdateResult updateFirst(Query query, Update update, Class<?> entityClass) {
        return getInvoker().invoke(() -> getImpl().updateFirst(query, update, entityClass));
    }

    @Override
    default UpdateResult updateFirst(final Query query, final Update update, final String collectionName) {
        return getInvoker().invoke(() -> getImpl().upsert(query, update, collectionName));
    }

    @Override
    default UpdateResult updateFirst(Query query, Update update, Class<?> entityClass, String collectionName) {
        return getInvoker().invoke(() -> getImpl().updateFirst(query, update, entityClass, collectionName));
    }

    @Override
    default UpdateResult updateMulti(Query query, Update update, Class<?> entityClass) {
        return getInvoker().invoke(() -> getImpl().updateMulti(query, update, entityClass));
    }

    @Override
    default UpdateResult updateMulti(final Query query, final Update update, String collectionName) {
        return getInvoker().invoke(() -> getImpl().updateMulti(query, update, collectionName));
    }

    @Override
    default UpdateResult updateMulti(final Query query, final Update update, Class<?> entityClass, String collectionName) {
        return getInvoker().invoke(() -> getImpl().updateMulti(query, update, entityClass, collectionName));
    }

    @Override
    default DeleteResult remove(Object object) {
        return getInvoker().invoke(() -> getImpl().remove(object));
    }

    @Override
    default DeleteResult remove(Object object, String collectionName) {
        return getInvoker().invoke(() -> getImpl().remove(object, collectionName));
    }

    @Override
    default DeleteResult remove(Query query, String collectionName) {
        return getInvoker().invoke(() -> getImpl().remove(query, collectionName));
    }

    @Override
    default DeleteResult remove(Query query, Class<?> entityClass) {
        return getInvoker().invoke(() -> getImpl().remove(query, entityClass));
    }

    @Override
    default DeleteResult remove(Query query, Class<?> entityClass, String collectionName) {
        return getInvoker().invoke(() -> getImpl().remove(query, entityClass, collectionName));
    }

    @Override
    default <T> List<T> findAll(Class<T> entityClass) {
        return getInvoker().invoke(() -> getImpl().findAll(entityClass));
    }

    @Override
    default <T> List<T> findAll(Class<T> entityClass, String collectionName) {
        return getInvoker().invoke(() -> getImpl().findAll(entityClass, collectionName));
    }

    @Override
    default <T> MapReduceResults<T> mapReduce(String inputCollectionName, String mapFunction, String reduceFunction, Class<T> entityClass) {
        return getInvoker().invoke(() -> getImpl().mapReduce(inputCollectionName, mapFunction, reduceFunction, entityClass));
    }

    @Override
    default <T> MapReduceResults<T> mapReduce(String inputCollectionName, String mapFunction, String reduceFunction, MapReduceOptions mapReduceOptions, Class<T> entityClass) {
        return getInvoker().invoke(() -> getImpl().mapReduce(inputCollectionName, mapFunction, reduceFunction, mapReduceOptions, entityClass));
    }

    @Override
    default <T> MapReduceResults<T> mapReduce(Query query, String inputCollectionName, String mapFunction, String reduceFunction, Class<T> entityClass) {
        return getInvoker().invoke(() -> getImpl().mapReduce(query, inputCollectionName, mapFunction, reduceFunction, entityClass));
    }

    @Override
    default <T> MapReduceResults<T> mapReduce(Query query, String inputCollectionName, String mapFunction, String reduceFunction, MapReduceOptions mapReduceOptions, Class<T> entityClass) {
        return getInvoker().invoke(() -> getImpl().mapReduce(query, inputCollectionName, mapFunction, reduceFunction, mapReduceOptions, entityClass));
    }

    @Override
    default <T> GroupByResults<T> group(String inputCollectionName, GroupBy groupBy, Class<T> entityClass) {
        return getInvoker().invoke(() -> getImpl().group(inputCollectionName, groupBy, entityClass));
    }

    @Override
    default <T> GroupByResults<T> group(Criteria criteria, String inputCollectionName, GroupBy groupBy, Class<T> entityClass) {
        return getInvoker().invoke(() -> getImpl().group(criteria, inputCollectionName, groupBy, entityClass));
    }

    @Override
    default <O> AggregationResults<O> aggregate(TypedAggregation<?> aggregation, Class<O> outputType) {
        return getInvoker().invoke(() -> getImpl().aggregate(aggregation, outputType));
    }

    @Override
    default <O> AggregationResults<O> aggregate(TypedAggregation<?> aggregation, String inputCollectionName, Class<O> outputType) {
        return getInvoker().invoke(() -> getImpl().aggregate(aggregation, inputCollectionName, outputType));
    }

    @Override
    default <O> AggregationResults<O> aggregate(Aggregation aggregation, Class<?> inputType, Class<O> outputType) {
        return getInvoker().invoke(() -> getImpl().aggregate(aggregation, inputType, outputType));
    }

    @Override
    default <O> AggregationResults<O> aggregate(Aggregation aggregation, String collectionName, Class<O> outputType) {
        return getInvoker().invoke(() -> getImpl().aggregate(aggregation, collectionName, outputType));
    }


    @Override
    default <O> CloseableIterator<O> aggregateStream(TypedAggregation<?> aggregation, String inputCollectionName, Class<O> outputType) {
        return new CloseableIteratorDecoratorImpl<>(getInvoker().invoke(() -> getImpl().aggregateStream(aggregation, inputCollectionName, outputType)), getInvoker());
    }

    @Override
    default <O> CloseableIterator<O> aggregateStream(TypedAggregation<?> aggregation, Class<O> outputType) {
        return new CloseableIteratorDecoratorImpl<>(getInvoker().invoke(() -> getImpl().aggregateStream(aggregation, outputType)), getInvoker());
    }

    @Override
    default <O> CloseableIterator<O> aggregateStream(Aggregation aggregation, Class<?> inputType, Class<O> outputType) {
        return new CloseableIteratorDecoratorImpl<>(getInvoker().invoke(() -> getImpl().aggregateStream(aggregation, inputType, outputType)), getInvoker());
    }

    @Override
    default <O> CloseableIterator<O> aggregateStream(Aggregation aggregation, String collectionName, Class<O> outputType) {
        return new CloseableIteratorDecoratorImpl<>(getInvoker().invoke(() -> getImpl().aggregateStream(aggregation, collectionName, outputType)), getInvoker());
    }


    @Override
    default <T> List<T> findAllAndRemove(Query query, String collectionName) {
        return getInvoker().invoke(() -> getImpl().findAllAndRemove(query, collectionName));
    }

    @Override
    default <T> List<T> findAllAndRemove(Query query, Class<T> entityClass) {
        return getInvoker().invoke(() -> getImpl().findAllAndRemove(query, entityClass));
    }

    @Override
    default <T> List<T> findAllAndRemove(Query query, Class<T> entityClass, String collectionName) {
        return getInvoker().invoke(() -> getImpl().findAllAndRemove(query, entityClass, collectionName));
    }






    @Override
    //This relies on passing the monitored instance of mongotemplate(this) to the ExecutableFind
    default <T> ExecutableFind<T> query(Class<T> domainType) {
        return new ExecutableFindDecoratorImpl<>(getInvoker().invoke(() -> getImpl().query(domainType)), getInvoker());
    }


    @Override
    //This relies on passing the monitored instance of mongotemplate(this) to the ExecutableUpdate
    default <T> ExecutableUpdate<T> update(Class<T> domainType) {
        return new ExecutableUpdateDecoratorImpl<>(getInvoker().invoke(() -> getImpl().update(domainType)), getInvoker());
    }

    @Override
    //This relies on passing the monitored instance of mongotemplate(this) to the ExecutableRemove
    default <T> ExecutableRemove<T> remove(Class<T> domainType) {
        return new ExecutableRemoveDecoratorImpl<>(getInvoker().invoke(() -> getImpl().remove(domainType)), getInvoker());
    }

    @Override
    //This relies on passing the monitored instance of mongotemplate(this) to the ExecutableAggregation
    default <T> ExecutableAggregation<T> aggregateAndReturn(Class<T> domainType) {
        return new ExecutableAggregationDecoratorImpl<>(getInvoker().invoke(() -> getImpl().aggregateAndReturn(domainType)), getInvoker());
    }

    @Override
    //This relies on passing the monitored instance of mongotemplate(this) to the ExecutableInsert
    default <T> ExecutableInsert<T> insert(Class<T> domainType) {
        return new ExecutableInsertDecoratorImpl<>(getInvoker().invoke(() -> getImpl().insert(domainType)), getInvoker());
    }





    @Override
    default Set<String> getCollectionNames() {
        return getInvoker().invoke(() -> getImpl().getCollectionNames());
    }


    @Override
    default <T> List<T> findDistinct(Query query, String field, Class<?> entityClass, Class<T> resultClass) {
        return getInvoker().invoke(() -> getImpl().findDistinct(query, field, entityClass, resultClass));
    }

    @Override
    default <T> List<T> findDistinct(Query query, String field, String collectionName, Class<?> entityClass, Class<T> resultClass) {
        return getInvoker().invoke(() -> getImpl().findDistinct(query, field, collectionName, entityClass, resultClass));
    }

    @Override
    default <S, T> T findAndReplace(Query query, S replacement, FindAndReplaceOptions options, Class<S> entityType, String collectionName, Class<T> resultType) {
        return getInvoker().invoke(() -> getImpl().findAndReplace(query, replacement, options, entityType, collectionName, resultType));
    }

    @Override
    default <T> MapReduceWithMapFunction<T> mapReduce(Class<T> domainType) {
        return getInvoker().invoke(() -> new MapReduceWithMapFunctionDecoratorImpl<>(getImpl().mapReduce(domainType), getInvoker()));
    }

    @Override
    default MongoOperations withSession(ClientSession session) {
        return new MongoOperationsDecoratorImpl(getInvoker().invoke(()-> getImpl().withSession(session)), getInvoker());
    }

    @Override
    default IndexOperations indexOps(String collectionName) {
        return getInvoker().invoke(() -> new IndexOperationsDecoratorImpl(getImpl().indexOps(collectionName), getInvoker()));
    }

    @Override
    default IndexOperations indexOps(Class<?> entityClass) {
        return getInvoker().invoke(() -> new IndexOperationsDecoratorImpl(getImpl().indexOps(entityClass), getInvoker()));
    }

    @Override
    default BulkOperations bulkOps(BulkOperations.BulkMode mode, String collectionName) {
        return new BulkOperationsDecoratorImpl(getInvoker().invoke(() -> getImpl().bulkOps(mode, collectionName)), getInvoker());
    }

    @Override
    default BulkOperations bulkOps(BulkOperations.BulkMode mode, Class<?> entityType) {
        return new BulkOperationsDecoratorImpl(getInvoker().invoke(() -> getImpl().bulkOps(mode, entityType)), getInvoker());
    }

    @Override
    default BulkOperations bulkOps(BulkOperations.BulkMode mode, Class<?> entityType, String collectionName) {
        return new BulkOperationsDecoratorImpl(getInvoker().invoke(() -> getImpl().bulkOps(mode, entityType, collectionName)), getInvoker());
    }

    @Override
    default ScriptOperations scriptOps() {
        return new ScriptOperationsDecoratorImpl(getInvoker().invoke(()-> getImpl().scriptOps()), getInvoker());
    }


    //TODO create decorator for sessionScoped
    @Override
    default SessionScoped withSession(ClientSessionOptions sessionOptions) {
        return new SessionScopedDecoratorImpl(getInvoker().invoke(()-> getImpl().withSession(sessionOptions)), getInvoker());//todo remove this
    }

    @Override
    default SessionScoped withSession(Supplier<ClientSession> sessionProvider) {
      return new SessionScopedDecoratorImpl(getInvoker().invoke(()-> getImpl().withSession(sessionProvider)), getInvoker());//todo remove this
    }


}
