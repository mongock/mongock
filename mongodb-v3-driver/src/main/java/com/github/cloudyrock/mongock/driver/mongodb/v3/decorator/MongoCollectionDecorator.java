package com.github.cloudyrock.mongock.driver.mongodb.v3.decorator;

import com.github.cloudyrock.mongock.annotations.NonLockGuarded;
import com.github.cloudyrock.mongock.annotations.NonLockGuardedType;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.v3.decorator.impl.AggregateIterableDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.v3.decorator.impl.ChangeStreamIterableDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.v3.decorator.impl.DistinctIterableDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.v3.decorator.impl.FindIterableDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.v3.decorator.impl.ListIndexesIterableDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.v3.decorator.impl.MapReduceIterableDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.v3.decorator.impl.MongoCollectionDecoratorImpl;
import com.mongodb.MongoNamespace;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.ClientSession;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.CreateIndexOptions;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.model.DropIndexOptions;
import com.mongodb.client.model.EstimatedDocumentCountOptions;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.model.RenameCollectionOptions;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import java.util.List;

public interface MongoCollectionDecorator<T> extends MongoCollection<T> {

  MongoCollection<T> getImpl();

  LockGuardInvoker getInvoker();

  @Override
  @NonLockGuarded(NonLockGuardedType.NONE)
  default MongoNamespace getNamespace() {
    return getImpl().getNamespace();
  }

  @Override
  @NonLockGuarded(NonLockGuardedType.NONE)
  default Class<T> getDocumentClass() {
    return getImpl().getDocumentClass();
  }

  @Override
  @NonLockGuarded(NonLockGuardedType.NONE)
  default CodecRegistry getCodecRegistry() {
    return getImpl().getCodecRegistry();
  }

  @Override
  @NonLockGuarded(NonLockGuardedType.NONE)
  default ReadPreference getReadPreference() {
    return getImpl().getReadPreference();
  }

  @Override
  @NonLockGuarded(NonLockGuardedType.NONE)
  default WriteConcern getWriteConcern() {
    return getImpl().getWriteConcern();
  }

  @Override
  @NonLockGuarded(NonLockGuardedType.NONE)
  default ReadConcern getReadConcern() {
    return getImpl().getReadConcern();
  }

  @Override
  @NonLockGuarded(NonLockGuardedType.METHOD)
  default <NewTDocument> MongoCollection<NewTDocument> withDocumentClass(Class<NewTDocument> aClass) {
    return new MongoCollectionDecoratorImpl<>(getImpl().withDocumentClass(aClass), getInvoker());
  }

  @Override
  @NonLockGuarded(NonLockGuardedType.METHOD)
  default MongoCollection<T> withCodecRegistry(CodecRegistry codecRegistry) {
    return new MongoCollectionDecoratorImpl<>(getImpl().withCodecRegistry(codecRegistry), getInvoker());
  }

  @Override
  @NonLockGuarded(NonLockGuardedType.METHOD)
  default MongoCollection<T> withReadPreference(ReadPreference readPreference) {
    return new MongoCollectionDecoratorImpl<>(getImpl().withReadPreference(readPreference), getInvoker());
  }

  @Override
  @NonLockGuarded(NonLockGuardedType.METHOD)
  default MongoCollection<T> withWriteConcern(WriteConcern writeConcern) {
    return new MongoCollectionDecoratorImpl<>(getImpl().withWriteConcern(writeConcern), getInvoker());
  }

  @Override
  @NonLockGuarded(NonLockGuardedType.METHOD)
  default MongoCollection<T> withReadConcern(ReadConcern readConcern) {
    return new MongoCollectionDecoratorImpl<>(getImpl().withReadConcern(readConcern), getInvoker());
  }

  @Override
  default long count() {
    return getInvoker().invoke(() -> getImpl().count());
  }

  @Override
  default long count(Bson bson) {
    return getInvoker().invoke(() -> getImpl().count(bson));
  }

  @Override
  default long count(Bson bson, CountOptions countOptions) {
    return getInvoker().invoke(() -> getImpl().count(bson, countOptions));
  }

  @Override
  default <TResult> DistinctIterable<TResult> distinct(String s, Class<TResult> aClass) {
    return new DistinctIterableDecoratorImpl<>(getInvoker().invoke(() -> getImpl().distinct(s, aClass)), getInvoker());
  }

  @Override
  default <TResult> DistinctIterable<TResult> distinct(String s, Bson bson, Class<TResult> aClass) {
    return new DistinctIterableDecoratorImpl<>(getInvoker().invoke(() -> getImpl().distinct(s, bson, aClass)), getInvoker());
  }

  @Override
  default FindIterable<T> find() {
    return new FindIterableDecoratorImpl<>(getInvoker().invoke(() -> getImpl().find()), getInvoker());
  }

  @Override
  default <TResult> FindIterable<TResult> find(Class<TResult> aClass) {
    return new FindIterableDecoratorImpl<>(getInvoker().invoke(() -> getImpl().find(aClass)), getInvoker());
  }

  @Override
  default FindIterable<T> find(Bson bson) {
    return new FindIterableDecoratorImpl<>(getInvoker().invoke(() -> getImpl().find(bson)), getInvoker());
  }

  @Override
  default <TResult> FindIterable<TResult> find(Bson bson, Class<TResult> aClass) {
    return new FindIterableDecoratorImpl<>(getInvoker().invoke(() -> getImpl().find(bson, aClass)), getInvoker());
  }

  @Override
  default AggregateIterable<T> aggregate(List<? extends Bson> list) {
    return new AggregateIterableDecoratorImpl<>(getInvoker().invoke(() -> getImpl().aggregate(list)), getInvoker());
  }

  @Override
  default <TResult> AggregateIterable<TResult> aggregate(List<? extends Bson> list, Class<TResult> aClass) {
    return new AggregateIterableDecoratorImpl<>(getInvoker().invoke(() -> getImpl().aggregate(list, aClass)), getInvoker());
  }

  @Override
  default MapReduceIterable<T> mapReduce(String s, String s1) {
    return new MapReduceIterableDecoratorImpl<>(getInvoker().invoke(() -> getImpl().mapReduce(s, s1)), getInvoker());
  }

  @Override
  default <TResult> MapReduceIterable<TResult> mapReduce(String s, String s1, Class<TResult> aClass) {
    return new MapReduceIterableDecoratorImpl<>(getInvoker().invoke(() -> getImpl().mapReduce(s, s1, aClass)), getInvoker());
  }

  @Override
  default BulkWriteResult bulkWrite(List<? extends WriteModel<? extends T>> list) {
    return getInvoker().invoke(() -> getImpl().bulkWrite(list));
  }

  @Override
  default BulkWriteResult bulkWrite(List<? extends WriteModel<? extends T>> list, BulkWriteOptions bulkWriteOptions) {
    return getInvoker().invoke(() -> getImpl().bulkWrite(list, bulkWriteOptions));
  }

  @Override
  default void insertOne(T t) {
    getInvoker().invoke(() -> getImpl().insertOne(t));
  }

  @Override
  default void insertOne(T t, InsertOneOptions insertOneOptions) {
    getInvoker().invoke(() -> getImpl().insertOne(t, insertOneOptions));
  }

  @Override
  default void insertMany(List<? extends T> list) {
    getInvoker().invoke(() -> getImpl().insertMany(list));
  }

  @Override
  default void insertMany(List<? extends T> list, InsertManyOptions insertManyOptions) {
    getInvoker().invoke(() -> getImpl().insertMany(list, insertManyOptions));
  }

  @Override
  default DeleteResult deleteOne(Bson bson) {
    return getInvoker().invoke(() -> getImpl().deleteOne(bson));
  }

  @Override
  default DeleteResult deleteOne(Bson bson, DeleteOptions deleteOptions) {
    return getInvoker().invoke(() -> getImpl().deleteOne(bson, deleteOptions));
  }

  @Override
  default DeleteResult deleteMany(Bson bson) {
    return getInvoker().invoke(() -> getImpl().deleteMany(bson));
  }

  @Override
  default DeleteResult deleteMany(Bson bson, DeleteOptions deleteOptions) {
    return getInvoker().invoke(() -> getImpl().deleteMany(bson, deleteOptions));
  }

  @Override
  default UpdateResult replaceOne(Bson bson, T t) {
    return getInvoker().invoke(() -> getImpl().replaceOne(bson, t));
  }

  @Override
  default UpdateResult replaceOne(Bson bson, T t, UpdateOptions updateOptions) {
    return getInvoker().invoke(() -> getImpl().replaceOne(bson, t, updateOptions));
  }

  @Override
  default UpdateResult updateOne(Bson bson, Bson bson1) {
    return getInvoker().invoke(() -> getImpl().updateOne(bson, bson1));
  }

  @Override
  default UpdateResult updateOne(Bson bson, Bson bson1, UpdateOptions updateOptions) {
    return getInvoker().invoke(() -> getImpl().updateOne(bson, bson1, updateOptions));
  }

  @Override
  default UpdateResult updateMany(Bson bson, Bson bson1) {
    return getInvoker().invoke(() -> getImpl().updateMany(bson, bson1));
  }

  @Override
  default UpdateResult updateMany(Bson bson, Bson bson1, UpdateOptions updateOptions) {
    return getInvoker().invoke(() -> getImpl().updateMany(bson, bson1, updateOptions));
  }

  @Override
  default T findOneAndDelete(Bson bson) {
    return getInvoker().invoke(() -> getImpl().findOneAndDelete(bson));
  }

  @Override
  default T findOneAndDelete(Bson bson, FindOneAndDeleteOptions findOneAndDeleteOptions) {
    return getInvoker().invoke(() -> getImpl().findOneAndDelete(bson, findOneAndDeleteOptions));
  }

  @Override
  default T findOneAndReplace(Bson bson, T t) {
    return getInvoker().invoke(() -> getImpl().findOneAndReplace(bson, t));
  }

  @Override
  default T findOneAndReplace(Bson bson, T t, FindOneAndReplaceOptions findOneAndReplaceOptions) {
    return getInvoker().invoke(() -> getImpl().findOneAndReplace(bson, t, findOneAndReplaceOptions));
  }

  @Override
  default T findOneAndUpdate(Bson bson, Bson bson1) {
    return getInvoker().invoke(() -> getImpl().findOneAndUpdate(bson, bson1));
  }

  @Override
  default T findOneAndUpdate(Bson bson, Bson bson1, FindOneAndUpdateOptions findOneAndUpdateOptions) {
    return getInvoker().invoke(() -> getImpl().findOneAndUpdate(bson, bson1, findOneAndUpdateOptions));
  }

  @Override
  default void drop() {
    getInvoker().invoke(() -> getImpl().drop());
  }

  @Override
  default String createIndex(Bson bson) {
    return getInvoker().invoke(() -> getImpl().createIndex(bson));
  }

  @Override
  default String createIndex(Bson bson, IndexOptions indexOptions) {
    return getInvoker().invoke(() -> getImpl().createIndex(bson, indexOptions));
  }

  @Override
  default List<String> createIndexes(List<IndexModel> list) {
    return getInvoker().invoke(() -> getImpl().createIndexes(list));
  }

  @Override
  default ListIndexesIterable<Document> listIndexes() {
    return new ListIndexesIterableDecoratorImpl<>(getInvoker().invoke(() -> getImpl().listIndexes()), getInvoker());
  }

  @Override
  default <TResult> ListIndexesIterable<TResult> listIndexes(Class<TResult> aClass) {
    return new ListIndexesIterableDecoratorImpl<>(getInvoker().invoke(() -> getImpl().listIndexes(aClass)), getInvoker());
  }

  @Override
  default void dropIndex(String s) {
    getInvoker().invoke(() -> getImpl().dropIndex(s));
  }

  @Override
  default void dropIndex(Bson bson) {
    getInvoker().invoke(() -> getImpl().dropIndex(bson));
  }

  @Override
  default void dropIndexes() {
    getInvoker().invoke(() -> getImpl().dropIndexes());
  }

  @Override
  default void renameCollection(MongoNamespace mongoNamespace) {
    getInvoker().invoke(() -> getImpl().renameCollection(mongoNamespace));
  }

  @Override
  default void renameCollection(MongoNamespace mongoNamespace, RenameCollectionOptions renameCollectionOptions) {
    getInvoker().invoke(() -> getImpl().renameCollection(mongoNamespace, renameCollectionOptions));
  }

  @Override
  default long count(ClientSession clientSession) {
    return getInvoker().invoke(() -> getImpl().count(clientSession));
  }

  @Override
  default long count(ClientSession clientSession, Bson bson) {
    return getInvoker().invoke(() -> getImpl().count(clientSession, bson));
  }

  @Override
  default long count(ClientSession clientSession, Bson bson, CountOptions countOptions) {
    return getInvoker().invoke(() -> getImpl().count(clientSession, bson, countOptions));
  }

  @Override
  default long countDocuments() {
    return getInvoker().invoke(() -> getImpl().countDocuments());
  }

  @Override
  default long countDocuments(Bson bson) {
    return getInvoker().invoke(() -> getImpl().countDocuments(bson));
  }

  @Override
  default long countDocuments(Bson bson, CountOptions countOptions) {
    return getInvoker().invoke(() -> getImpl().countDocuments(bson, countOptions));
  }

  @Override
  default long countDocuments(ClientSession clientSession) {
    return getInvoker().invoke(() -> getImpl().countDocuments(clientSession));
  }

  @Override
  default long countDocuments(ClientSession clientSession, Bson bson) {
    return getInvoker().invoke(() -> getImpl().countDocuments(clientSession, bson));
  }

  @Override
  default long countDocuments(ClientSession clientSession, Bson bson, CountOptions countOptions) {
    return getInvoker().invoke(() -> getImpl().countDocuments(clientSession, bson, countOptions));
  }

  @Override
  default long estimatedDocumentCount() {
    return getInvoker().invoke(() -> getImpl().estimatedDocumentCount());
  }

  @Override
  default long estimatedDocumentCount(EstimatedDocumentCountOptions estimatedDocumentCountOptions) {
    return getInvoker().invoke(() -> getImpl().estimatedDocumentCount(estimatedDocumentCountOptions));
  }

  @Override
  default <TResult> DistinctIterable<TResult> distinct(ClientSession clientSession, String s, Class<TResult> aClass) {
    return new DistinctIterableDecoratorImpl<>(getInvoker().invoke(()-> getImpl().distinct(clientSession, s, aClass)), getInvoker());
  }

  @Override
  default <TResult> DistinctIterable<TResult> distinct(ClientSession clientSession, String s, Bson bson, Class<TResult> aClass) {
    return new DistinctIterableDecoratorImpl<>(getInvoker().invoke(()-> getImpl().distinct(clientSession, s, bson, aClass)), getInvoker());
  }

  @Override
  default FindIterable<T> find(ClientSession clientSession) {
    return new FindIterableDecoratorImpl<>(getInvoker().invoke(()-> getImpl().find(clientSession)), getInvoker());
  }

  @Override
  default <TResult> FindIterable<TResult> find(ClientSession clientSession, Class<TResult> aClass) {
    return new FindIterableDecoratorImpl<>(getInvoker().invoke(()-> getImpl().find(clientSession, aClass)), getInvoker());
  }

  @Override
  default FindIterable<T> find(ClientSession clientSession, Bson bson) {
    return new FindIterableDecoratorImpl<>(getInvoker().invoke(()-> getImpl().find(clientSession, bson)), getInvoker());
  }

  @Override
  default <TResult> FindIterable<TResult> find(ClientSession clientSession, Bson bson, Class<TResult> aClass) {
    return new FindIterableDecoratorImpl<>(getInvoker().invoke(()-> getImpl().find(clientSession, bson, aClass)), getInvoker());
  }

  @Override
  default AggregateIterable<T> aggregate(ClientSession clientSession, List<? extends Bson> list) {
    return new AggregateIterableDecoratorImpl<>(getInvoker().invoke(()-> getImpl().aggregate(clientSession, list)), getInvoker());
  }

  @Override
  default <TResult> AggregateIterable<TResult> aggregate(ClientSession clientSession, List<? extends Bson> list, Class<TResult> aClass) {
    return new AggregateIterableDecoratorImpl<>(getInvoker().invoke(()-> getImpl().aggregate(clientSession, list, aClass)), getInvoker());
  }

  @Override
  default ChangeStreamIterable<T> watch() {
    return new ChangeStreamIterableDecoratorImpl<>(getInvoker().invoke(()-> getImpl().watch()), getInvoker());
  }

  @Override
  default <TResult> ChangeStreamIterable<TResult> watch(Class<TResult> aClass) {
    return new ChangeStreamIterableDecoratorImpl<>(getInvoker().invoke(()-> getImpl().watch(aClass)), getInvoker());

  }

  @Override
  default ChangeStreamIterable<T> watch(List<? extends Bson> list) {
    return new ChangeStreamIterableDecoratorImpl<>(getInvoker().invoke(()-> getImpl().watch(list)), getInvoker());
  }

  @Override
  default <TResult> ChangeStreamIterable<TResult> watch(List<? extends Bson> list, Class<TResult> aClass) {
    return new ChangeStreamIterableDecoratorImpl<>(getInvoker().invoke(()-> getImpl().watch(list, aClass)), getInvoker());
  }

  @Override
  default ChangeStreamIterable<T> watch(ClientSession clientSession) {
    return new ChangeStreamIterableDecoratorImpl<>(getInvoker().invoke(()-> getImpl().watch(clientSession)), getInvoker());
  }

  @Override
  default <TResult> ChangeStreamIterable<TResult> watch(ClientSession clientSession, Class<TResult> aClass) {
    return new ChangeStreamIterableDecoratorImpl<>(getInvoker().invoke(()-> getImpl().watch(clientSession, aClass)), getInvoker());
  }

  @Override
  default ChangeStreamIterable<T> watch(ClientSession clientSession, List<? extends Bson> list) {
    return new ChangeStreamIterableDecoratorImpl<>(getInvoker().invoke(()-> getImpl().watch(clientSession, list)), getInvoker());
  }

  @Override
  default <TResult> ChangeStreamIterable<TResult> watch(ClientSession clientSession, List<? extends Bson> list, Class<TResult> aClass) {
    return new ChangeStreamIterableDecoratorImpl<>(getInvoker().invoke(()-> getImpl().watch(clientSession, list, aClass)), getInvoker());
  }

  @Override
  default MapReduceIterable<T> mapReduce(ClientSession clientSession, String s, String s1) {
    return new MapReduceIterableDecoratorImpl<>(getInvoker().invoke(()-> getImpl().mapReduce(clientSession, s, s1)), getInvoker());
  }

  @Override
  default <TResult> MapReduceIterable<TResult> mapReduce(ClientSession clientSession, String s, String s1, Class<TResult> aClass) {
    return new MapReduceIterableDecoratorImpl<>(getInvoker().invoke(()-> getImpl().mapReduce(clientSession, s, s1, aClass)), getInvoker());
  }

  @Override
  default BulkWriteResult bulkWrite(ClientSession clientSession, List<? extends WriteModel<? extends T>> list) {
    return getInvoker().invoke(()-> getImpl().bulkWrite(clientSession, list));
  }

  @Override
  default BulkWriteResult bulkWrite(ClientSession clientSession, List<? extends WriteModel<? extends T>> list, BulkWriteOptions bulkWriteOptions) {
    return getInvoker().invoke(()-> getImpl().bulkWrite(clientSession, list, bulkWriteOptions));
  }

  @Override
  default void insertOne(ClientSession clientSession, T t) {
    getInvoker().invoke(()-> getImpl().insertOne(clientSession, t));
  }

  @Override
  default void insertOne(ClientSession clientSession, T t, InsertOneOptions insertOneOptions) {
    getInvoker().invoke(()-> getImpl().insertOne(clientSession, t, insertOneOptions));
  }

  @Override
  default void insertMany(ClientSession clientSession, List<? extends T> list) {
    getInvoker().invoke(()-> getImpl().insertMany(clientSession, list));
  }

  @Override
  default void insertMany(ClientSession clientSession, List<? extends T> list, InsertManyOptions insertManyOptions) {
    getInvoker().invoke(()-> getImpl().insertMany(clientSession, list, insertManyOptions));
  }

  @Override
  default DeleteResult deleteOne(ClientSession clientSession, Bson bson) {
    return getInvoker().invoke(()-> getImpl().deleteOne(clientSession, bson));
  }

  @Override
  default DeleteResult deleteOne(ClientSession clientSession, Bson bson, DeleteOptions deleteOptions) {
    return getInvoker().invoke(()-> getImpl().deleteOne(clientSession, bson, deleteOptions));
  }

  @Override
  default DeleteResult deleteMany(ClientSession clientSession, Bson bson) {
    return getInvoker().invoke(()-> getImpl().deleteMany(clientSession, bson));
  }

  @Override
  default DeleteResult deleteMany(ClientSession clientSession, Bson bson, DeleteOptions deleteOptions) {
    return getInvoker().invoke(()-> getImpl().deleteMany(clientSession, bson, deleteOptions));
  }

  @Override
  default UpdateResult replaceOne(Bson bson, T t, ReplaceOptions replaceOptions) {
    return getInvoker().invoke(()-> getImpl().replaceOne(bson, t, replaceOptions));
  }

  @Override
  default UpdateResult replaceOne(ClientSession clientSession, Bson bson, T t) {
    return getInvoker().invoke(()-> getImpl().replaceOne(clientSession, bson,t));
  }

  @Override
  default UpdateResult replaceOne(ClientSession clientSession, Bson bson, T t, UpdateOptions updateOptions) {
    return getInvoker().invoke(()-> getImpl().replaceOne(clientSession, bson,t, updateOptions));
  }

  @Override
  default UpdateResult replaceOne(ClientSession clientSession, Bson bson, T t, ReplaceOptions replaceOptions) {
    return getInvoker().invoke(()-> getImpl().replaceOne(clientSession, bson,t, replaceOptions));
  }

  @Override
  default UpdateResult updateOne(ClientSession clientSession, Bson bson, Bson bson1) {
    return getInvoker().invoke(()-> getImpl().updateOne(clientSession, bson, bson1));
  }

  @Override
  default UpdateResult updateOne(ClientSession clientSession, Bson bson, Bson bson1, UpdateOptions updateOptions) {
    return getInvoker().invoke(()-> getImpl().updateOne(clientSession, bson, bson1, updateOptions));
  }

  @Override
  default UpdateResult updateOne(Bson bson, List<? extends Bson> list) {
    return getInvoker().invoke(()-> getImpl().updateOne(bson, list));
  }

  @Override
  default UpdateResult updateOne(Bson bson, List<? extends Bson> list, UpdateOptions updateOptions) {
    return getInvoker().invoke(()-> getImpl().updateOne(bson, list, updateOptions));
  }

  @Override
  default UpdateResult updateOne(ClientSession clientSession, Bson bson, List<? extends Bson> list) {
    return getInvoker().invoke(()-> getImpl().updateOne(clientSession, bson, list));
  }

  @Override
  default UpdateResult updateOne(ClientSession clientSession, Bson bson, List<? extends Bson> list, UpdateOptions updateOptions) {
    return getInvoker().invoke(()-> getImpl().updateOne(clientSession, bson, list, updateOptions));
  }

  @Override
  default UpdateResult updateMany(ClientSession clientSession, Bson bson, Bson bson1) {
    return getInvoker().invoke(()-> getImpl().updateMany(clientSession, bson, bson1));
  }

  @Override
  default UpdateResult updateMany(ClientSession clientSession, Bson bson, Bson bson1, UpdateOptions updateOptions) {
    return getInvoker().invoke(()-> getImpl().updateMany(clientSession, bson, bson1, updateOptions));
  }

  @Override
  default UpdateResult updateMany(Bson bson, List<? extends Bson> list) {
    return getInvoker().invoke(()-> getImpl().updateMany(bson, list));
  }

  @Override
  default UpdateResult updateMany(Bson bson, List<? extends Bson> list, UpdateOptions updateOptions) {
    return getInvoker().invoke(()-> getImpl().updateMany(bson, list, updateOptions));
  }

  @Override
  default UpdateResult updateMany(ClientSession clientSession, Bson bson, List<? extends Bson> list) {
    return getInvoker().invoke(()-> getImpl().updateMany(clientSession, bson, list));
  }

  @Override
  default UpdateResult updateMany(ClientSession clientSession, Bson bson, List<? extends Bson> list, UpdateOptions updateOptions) {
    return getInvoker().invoke(()-> getImpl().updateMany(clientSession, bson, list, updateOptions));
  }

  @Override
  default T findOneAndDelete(ClientSession clientSession, Bson bson) {
    return getInvoker().invoke(()-> getImpl().findOneAndDelete(clientSession, bson));
  }

  @Override
  default T findOneAndDelete(ClientSession clientSession, Bson bson, FindOneAndDeleteOptions findOneAndDeleteOptions) {
    return getInvoker().invoke(()-> getImpl().findOneAndDelete(clientSession, bson, findOneAndDeleteOptions));
  }

  @Override
  default T findOneAndReplace(ClientSession clientSession, Bson bson, T t) {
    return getInvoker().invoke(()-> getImpl().findOneAndReplace(clientSession, bson, t));
  }

  @Override
  default T findOneAndReplace(ClientSession clientSession, Bson bson, T t, FindOneAndReplaceOptions findOneAndReplaceOptions) {
    return getInvoker().invoke(()-> getImpl().findOneAndReplace(clientSession, bson, t, findOneAndReplaceOptions));
  }

  @Override
  default T findOneAndUpdate(ClientSession clientSession, Bson bson, Bson bson1) {
    return getInvoker().invoke(()-> getImpl().findOneAndUpdate(clientSession, bson, bson1));
  }

  @Override
  default T findOneAndUpdate(ClientSession clientSession, Bson bson, Bson bson1, FindOneAndUpdateOptions findOneAndUpdateOptions) {
    return getInvoker().invoke(()-> getImpl().findOneAndUpdate(clientSession, bson, bson1, findOneAndUpdateOptions));
  }

  @Override
  default T findOneAndUpdate(Bson bson, List<? extends Bson> list) {
    return getInvoker().invoke(()-> getImpl().findOneAndUpdate(bson, list));
  }

  @Override
  default T findOneAndUpdate(Bson bson, List<? extends Bson> list, FindOneAndUpdateOptions findOneAndUpdateOptions) {
    return getInvoker().invoke(()-> getImpl().findOneAndUpdate(bson, list, findOneAndUpdateOptions));
  }

  @Override
  default T findOneAndUpdate(ClientSession clientSession, Bson bson, List<? extends Bson> list) {
    return getInvoker().invoke(()-> getImpl().findOneAndUpdate(clientSession, bson, list));
  }

  @Override
  default T findOneAndUpdate(ClientSession clientSession, Bson bson, List<? extends Bson> list, FindOneAndUpdateOptions findOneAndUpdateOptions) {
    return getInvoker().invoke(()-> getImpl().findOneAndUpdate(clientSession, bson, list, findOneAndUpdateOptions));
  }

  @Override
  default void drop(ClientSession clientSession) {
    getInvoker().invoke(()-> getImpl().drop(clientSession));
  }

  @Override
  default String createIndex(ClientSession clientSession, Bson bson) {
    return getInvoker().invoke(()-> getImpl().createIndex(clientSession, bson));
  }

  @Override
  default String createIndex(ClientSession clientSession, Bson bson, IndexOptions indexOptions) {
    return getInvoker().invoke(()-> getImpl().createIndex(clientSession, bson, indexOptions));
  }

  @Override
  default List<String> createIndexes(List<IndexModel> list, CreateIndexOptions createIndexOptions) {
    return getInvoker().invoke(()-> getImpl().createIndexes(list, createIndexOptions));
  }

  @Override
  default List<String> createIndexes(ClientSession clientSession, List<IndexModel> list) {
    return getInvoker().invoke(()-> getImpl().createIndexes(clientSession, list));
  }

  @Override
  default List<String> createIndexes(ClientSession clientSession, List<IndexModel> list, CreateIndexOptions createIndexOptions) {
    return getInvoker().invoke(()-> getImpl().createIndexes(clientSession, list, createIndexOptions));
  }

  @Override
  default ListIndexesIterable<Document> listIndexes(ClientSession clientSession) {
    return new ListIndexesIterableDecoratorImpl<>(getInvoker().invoke(()-> getImpl().listIndexes(clientSession)), getInvoker());
  }

  @Override
  default <TResult> ListIndexesIterable<TResult> listIndexes(ClientSession clientSession, Class<TResult> aClass) {
    return new ListIndexesIterableDecoratorImpl<>(getInvoker().invoke(()-> getImpl().listIndexes(clientSession, aClass)), getInvoker());
  }

  @Override
  default void dropIndex(String s, DropIndexOptions dropIndexOptions) {
    getInvoker().invoke(()-> getImpl().dropIndex(s, dropIndexOptions));
  }

  @Override
  default void dropIndex(Bson bson, DropIndexOptions dropIndexOptions) {
    getInvoker().invoke(()-> getImpl().dropIndex(bson, dropIndexOptions));
  }

  @Override
  default void dropIndex(ClientSession clientSession, String s) {
    getInvoker().invoke(()-> getImpl().dropIndex(clientSession, s));
  }

  @Override
  default void dropIndex(ClientSession clientSession, Bson bson) {
    getInvoker().invoke(()-> getImpl().dropIndex(clientSession, bson));
  }

  @Override
  default void dropIndex(ClientSession clientSession, String s, DropIndexOptions dropIndexOptions) {
    getInvoker().invoke(()-> getImpl().dropIndex(clientSession, s, dropIndexOptions));
  }

  @Override
  default void dropIndex(ClientSession clientSession, Bson bson, DropIndexOptions dropIndexOptions) {
    getInvoker().invoke(()-> getImpl().dropIndex(clientSession, bson, dropIndexOptions));
  }

  @Override
  default void dropIndexes(ClientSession clientSession) {
    getInvoker().invoke(()-> getImpl().dropIndexes(clientSession));
  }

  @Override
  default void dropIndexes(DropIndexOptions dropIndexOptions) {
    getInvoker().invoke(()-> getImpl().dropIndexes(dropIndexOptions));
  }

  @Override
  default void dropIndexes(ClientSession clientSession, DropIndexOptions dropIndexOptions) {
    getInvoker().invoke(()-> getImpl().dropIndexes(clientSession, dropIndexOptions));
  }

  @Override
  default void renameCollection(ClientSession clientSession, MongoNamespace mongoNamespace) {
    getInvoker().invoke(()-> getImpl().renameCollection(clientSession, mongoNamespace));
  }

  @Override
  default void renameCollection(ClientSession clientSession, MongoNamespace mongoNamespace, RenameCollectionOptions renameCollectionOptions) {
    getInvoker().invoke(()-> getImpl().renameCollection(clientSession, mongoNamespace, renameCollectionOptions));
  }
}
