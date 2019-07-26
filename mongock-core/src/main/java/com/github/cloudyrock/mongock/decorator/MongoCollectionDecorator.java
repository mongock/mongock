package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.decorator.impl.*;
import com.github.cloudyrock.mongock.decorator.util.LockCheckInvoker;
import com.mongodb.MongoNamespace;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import java.util.List;

public interface MongoCollectionDecorator<T> extends MongoCollection<T> {

  MongoCollection<T> getImpl();

  LockCheckInvoker getInvoker();

  @Override
  default MongoNamespace getNamespace() {
    return getImpl().getNamespace();
  }

  @Override
  default Class<T> getDocumentClass() {
    return getImpl().getDocumentClass();
  }

  @Override
  default CodecRegistry getCodecRegistry() {
    return getImpl().getCodecRegistry();
  }

  @Override
  default ReadPreference getReadPreference() {
    return getImpl().getReadPreference();
  }

  @Override
  default WriteConcern getWriteConcern() {
    return getImpl().getWriteConcern();
  }

  @Override
  default ReadConcern getReadConcern() {
    return getImpl().getReadConcern();
  }

  @Override
  default <NewTDocument> MongoCollection<NewTDocument> withDocumentClass(Class<NewTDocument> aClass) {
    return new MongoCollectionDecoratorImpl<>(getImpl().withDocumentClass(aClass), getInvoker());
  }

  @Override
  default MongoCollection<T> withCodecRegistry(CodecRegistry codecRegistry) {
    return new MongoCollectionDecoratorImpl<>(getImpl().withCodecRegistry(codecRegistry), getInvoker());
  }

  @Override
  default MongoCollection<T> withReadPreference(ReadPreference readPreference) {
    return new MongoCollectionDecoratorImpl<>(getImpl().withReadPreference(readPreference), getInvoker());
  }

  @Override
  default MongoCollection<T> withWriteConcern(WriteConcern writeConcern) {
    return new MongoCollectionDecoratorImpl<>(getImpl().withWriteConcern(writeConcern), getInvoker());
  }

  @Override
  default MongoCollection<T> withReadConcern(ReadConcern readConcern) {
    return new MongoCollectionDecoratorImpl<>(getImpl().withReadConcern(readConcern), getInvoker());
  }

  @Override
  default long count() {
    return getInvoker().invoke(()-> getImpl().count());
  }

  @Override
  default long count(Bson bson) {
    return getInvoker().invoke(()-> getImpl().count(bson));
  }

  @Override
  default long count(Bson bson, CountOptions countOptions) {
    return getInvoker().invoke(()-> getImpl().count(bson, countOptions));
  }

  @Override
  default <TResult> DistinctIterable<TResult> distinct(String s, Class<TResult> aClass) {
    return getInvoker().invoke(()-> new DistinctIterableDecoratorImpl<>(getImpl().distinct(s, aClass), getInvoker()));
  }

  @Override
  default <TResult> DistinctIterable<TResult> distinct(String s, Bson bson, Class<TResult> aClass) {
    return new DistinctIterableDecoratorImpl<>(getInvoker().invoke(()->getImpl().distinct(s, bson, aClass)), getInvoker());
  }

  @Override
  default FindIterable<T> find() {
    return new FindIterableDecoratorImpl<>(getInvoker().invoke(()->getImpl().find()), getInvoker());
  }

  @Override
  default <TResult> FindIterable<TResult> find(Class<TResult> aClass) {
    return new FindIterableDecoratorImpl<>(getInvoker().invoke(()->getImpl().find(aClass)), getInvoker());
  }

  @Override
  default FindIterable<T> find(Bson bson) {
    return new FindIterableDecoratorImpl<>(getInvoker().invoke(()->getImpl().find(bson)), getInvoker());
  }

  @Override
  default <TResult> FindIterable<TResult> find(Bson bson, Class<TResult> aClass) {
    return new FindIterableDecoratorImpl<>(getInvoker().invoke(()->getImpl().find(bson, aClass)), getInvoker());
  }

  @Override
  default AggregateIterable<T> aggregate(List<? extends Bson> list) {
    return new AggregateIterableDecoratorImpl<>(getInvoker().invoke(()->getImpl().aggregate(list)), getInvoker());
  }

  @Override
  default <TResult> AggregateIterable<TResult> aggregate(List<? extends Bson> list, Class<TResult> aClass) {
    return new AggregateIterableDecoratorImpl<>(getInvoker().invoke(()->getImpl().aggregate(list, aClass)), getInvoker());
  }

  @Override
  default MapReduceIterable<T> mapReduce(String s, String s1) {
    return new MapReduceIterableDecoratorImpl<>(getInvoker().invoke(()->getImpl().mapReduce(s, s1)), getInvoker());
  }

  @Override
  default <TResult> MapReduceIterable<TResult> mapReduce(String s, String s1, Class<TResult> aClass) {
    return new MapReduceIterableDecoratorImpl<>(getInvoker().invoke(()->getImpl().mapReduce(s, s1, aClass)), getInvoker());
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
    return getInvoker().invoke(() -> getImpl().deleteOne(bson, deleteOptions));
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
}
