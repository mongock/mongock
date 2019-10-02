package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.decorator.impl.*;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
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

  MethodInvoker getLockChecker();

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
    return new MongoCollectionDecoratorImpl<>(getImpl().withDocumentClass(aClass), getLockChecker());
  }

  @Override
  default MongoCollection<T> withCodecRegistry(CodecRegistry codecRegistry) {
    return new MongoCollectionDecoratorImpl<>(getImpl().withCodecRegistry(codecRegistry), getLockChecker());
  }

  @Override
  default MongoCollection<T> withReadPreference(ReadPreference readPreference) {
    return new MongoCollectionDecoratorImpl<>(getImpl().withReadPreference(readPreference), getLockChecker());
  }

  @Override
  default MongoCollection<T> withWriteConcern(WriteConcern writeConcern) {
    return new MongoCollectionDecoratorImpl<>(getImpl().withWriteConcern(writeConcern), getLockChecker());
  }

  @Override
  default MongoCollection<T> withReadConcern(ReadConcern readConcern) {
    return new MongoCollectionDecoratorImpl<>(getImpl().withReadConcern(readConcern), getLockChecker());
  }

  @Override
  default long count() {
    return getLockChecker().invoke(() -> getImpl().count());
  }

  @Override
  default long count(Bson bson) {
    return getLockChecker().invoke(() -> getImpl().count(bson));
  }

  @Override
  default long count(Bson bson, CountOptions countOptions) {
    return getLockChecker().invoke(() -> getImpl().count(bson, countOptions));
  }

  @Override
  default <TResult> DistinctIterable<TResult> distinct(String s, Class<TResult> aClass) {
    return getLockChecker().invoke(() -> new DistinctIterableDecoratorImpl<>(getImpl().distinct(s, aClass), getLockChecker()));
  }

  @Override
  default <TResult> DistinctIterable<TResult> distinct(String s, Bson bson, Class<TResult> aClass) {
    return new DistinctIterableDecoratorImpl<>(getLockChecker().invoke(() -> getImpl().distinct(s, bson, aClass)), getLockChecker());
  }

  @Override
  default FindIterable<T> find() {
    return new FindIterableDecoratorImpl<>(getLockChecker().invoke(() -> getImpl().find()), getLockChecker());
  }

  @Override
  default <TResult> FindIterable<TResult> find(Class<TResult> aClass) {
    return new FindIterableDecoratorImpl<>(getLockChecker().invoke(() -> getImpl().find(aClass)), getLockChecker());
  }

  @Override
  default FindIterable<T> find(Bson bson) {
    return new FindIterableDecoratorImpl<>(getLockChecker().invoke(() -> getImpl().find(bson)), getLockChecker());
  }

  @Override
  default <TResult> FindIterable<TResult> find(Bson bson, Class<TResult> aClass) {
    return new FindIterableDecoratorImpl<>(getLockChecker().invoke(() -> getImpl().find(bson, aClass)), getLockChecker());
  }

  @Override
  default AggregateIterable<T> aggregate(List<? extends Bson> list) {
    return new AggregateIterableDecoratorImpl<>(getLockChecker().invoke(() -> getImpl().aggregate(list)), getLockChecker());
  }

  @Override
  default <TResult> AggregateIterable<TResult> aggregate(List<? extends Bson> list, Class<TResult> aClass) {
    return new AggregateIterableDecoratorImpl<>(getLockChecker().invoke(() -> getImpl().aggregate(list, aClass)), getLockChecker());
  }

  @Override
  default MapReduceIterable<T> mapReduce(String s, String s1) {
    return new MapReduceIterableDecoratorImpl<>(getLockChecker().invoke(() -> getImpl().mapReduce(s, s1)), getLockChecker());
  }

  @Override
  default <TResult> MapReduceIterable<TResult> mapReduce(String s, String s1, Class<TResult> aClass) {
    return new MapReduceIterableDecoratorImpl<>(getLockChecker().invoke(() -> getImpl().mapReduce(s, s1, aClass)), getLockChecker());
  }

  @Override
  default BulkWriteResult bulkWrite(List<? extends WriteModel<? extends T>> list) {
    return getLockChecker().invoke(() -> getImpl().bulkWrite(list));
  }

  @Override
  default BulkWriteResult bulkWrite(List<? extends WriteModel<? extends T>> list, BulkWriteOptions bulkWriteOptions) {
    return getLockChecker().invoke(() -> getImpl().bulkWrite(list, bulkWriteOptions));
  }

  @Override
  default void insertOne(T t) {
    getLockChecker().invoke(() -> getImpl().insertOne(t));
  }

  @Override
  default void insertOne(T t, InsertOneOptions insertOneOptions) {
    getLockChecker().invoke(() -> getImpl().insertOne(t, insertOneOptions));
  }

  @Override
  default void insertMany(List<? extends T> list) {
    getLockChecker().invoke(() -> getImpl().insertMany(list));
  }

  @Override
  default void insertMany(List<? extends T> list, InsertManyOptions insertManyOptions) {
    getLockChecker().invoke(() -> getImpl().insertMany(list, insertManyOptions));
  }

  @Override
  default DeleteResult deleteOne(Bson bson) {
    return getLockChecker().invoke(() -> getImpl().deleteOne(bson));
  }

  @Override
  default DeleteResult deleteOne(Bson bson, DeleteOptions deleteOptions) {
    return getLockChecker().invoke(() -> getImpl().deleteOne(bson, deleteOptions));
  }

  @Override
  default DeleteResult deleteMany(Bson bson) {
    return getLockChecker().invoke(() -> getImpl().deleteMany(bson));
  }

  @Override
  default DeleteResult deleteMany(Bson bson, DeleteOptions deleteOptions) {
    return getLockChecker().invoke(() -> getImpl().deleteOne(bson, deleteOptions));
  }

  @Override
  default UpdateResult replaceOne(Bson bson, T t) {
    return getLockChecker().invoke(() -> getImpl().replaceOne(bson, t));
  }

  @Override
  default UpdateResult replaceOne(Bson bson, T t, UpdateOptions updateOptions) {
    return getLockChecker().invoke(() -> getImpl().replaceOne(bson, t, updateOptions));
  }

  @Override
  default UpdateResult updateOne(Bson bson, Bson bson1) {
    return getLockChecker().invoke(() -> getImpl().updateOne(bson, bson1));
  }

  @Override
  default UpdateResult updateOne(Bson bson, Bson bson1, UpdateOptions updateOptions) {
    return getLockChecker().invoke(() -> getImpl().updateOne(bson, bson1, updateOptions));
  }

  @Override
  default UpdateResult updateMany(Bson bson, Bson bson1) {
    return getLockChecker().invoke(() -> getImpl().updateMany(bson, bson1));
  }

  @Override
  default UpdateResult updateMany(Bson bson, Bson bson1, UpdateOptions updateOptions) {
    return getLockChecker().invoke(() -> getImpl().updateMany(bson, bson1, updateOptions));
  }

  @Override
  default T findOneAndDelete(Bson bson) {
    return getLockChecker().invoke(() -> getImpl().findOneAndDelete(bson));
  }

  @Override
  default T findOneAndDelete(Bson bson, FindOneAndDeleteOptions findOneAndDeleteOptions) {
    return getLockChecker().invoke(() -> getImpl().findOneAndDelete(bson, findOneAndDeleteOptions));
  }

  @Override
  default T findOneAndReplace(Bson bson, T t) {
    return getLockChecker().invoke(() -> getImpl().findOneAndReplace(bson, t));
  }

  @Override
  default T findOneAndReplace(Bson bson, T t, FindOneAndReplaceOptions findOneAndReplaceOptions) {
    return getLockChecker().invoke(() -> getImpl().findOneAndReplace(bson, t, findOneAndReplaceOptions));
  }

  @Override
  default T findOneAndUpdate(Bson bson, Bson bson1) {
    return getLockChecker().invoke(() -> getImpl().findOneAndUpdate(bson, bson1));
  }

  @Override
  default T findOneAndUpdate(Bson bson, Bson bson1, FindOneAndUpdateOptions findOneAndUpdateOptions) {
    return getLockChecker().invoke(() -> getImpl().findOneAndUpdate(bson, bson1, findOneAndUpdateOptions));
  }

  @Override
  default void drop() {
    getLockChecker().invoke(() -> getImpl().drop());
  }

  @Override
  default String createIndex(Bson bson) {
    return getLockChecker().invoke(() -> getImpl().createIndex(bson));
  }

  @Override
  default String createIndex(Bson bson, IndexOptions indexOptions) {
    return getLockChecker().invoke(() -> getImpl().createIndex(bson, indexOptions));
  }

  @Override
  default List<String> createIndexes(List<IndexModel> list) {
    return getLockChecker().invoke(() -> getImpl().createIndexes(list));
  }

  @Override
  default ListIndexesIterable<Document> listIndexes() {
    return new ListIndexesIterableDecoratorImpl<>(getLockChecker().invoke(() -> getImpl().listIndexes()), getLockChecker());
  }

  @Override
  default <TResult> ListIndexesIterable<TResult> listIndexes(Class<TResult> aClass) {
    return new ListIndexesIterableDecoratorImpl<>(getLockChecker().invoke(() -> getImpl().listIndexes(aClass)), getLockChecker());
  }

  @Override
  default void dropIndex(String s) {
    getLockChecker().invoke(() -> getImpl().dropIndex(s));
  }

  @Override
  default void dropIndex(Bson bson) {
    getLockChecker().invoke(() -> getImpl().dropIndex(bson));
  }

  @Override
  default void dropIndexes() {
    getLockChecker().invoke(() -> getImpl().dropIndexes());
  }

  @Override
  default void renameCollection(MongoNamespace mongoNamespace) {
    getLockChecker().invoke(() -> getImpl().renameCollection(mongoNamespace));
  }

  @Override
  default void renameCollection(MongoNamespace mongoNamespace, RenameCollectionOptions renameCollectionOptions) {
    getLockChecker().invoke(() -> getImpl().renameCollection(mongoNamespace, renameCollectionOptions));
  }
























  @Override
  default long count(ClientSession clientSession) {
    return getLockChecker().invoke(() -> getImpl().count(clientSession));
  }

  @Override
  default long count(ClientSession clientSession, Bson bson) {
    return getLockChecker().invoke(() -> getImpl().count(clientSession, bson));
  }

  @Override
  default long count(ClientSession clientSession, Bson bson, CountOptions countOptions) {
    return getLockChecker().invoke(() -> getImpl().count(clientSession, bson, countOptions));
  }

  @Override
  default long countDocuments() {
    return getLockChecker().invoke(() -> getImpl().countDocuments());
  }

  @Override
  default long countDocuments(Bson bson) {
    return getLockChecker().invoke(() -> getImpl().countDocuments(bson));
  }

  @Override
  default long countDocuments(Bson bson, CountOptions countOptions) {
    return getLockChecker().invoke(() -> getImpl().countDocuments(bson, countOptions));
  }

  @Override
  default long countDocuments(ClientSession clientSession) {
    return getLockChecker().invoke(() -> getImpl().countDocuments(clientSession));
  }

  @Override
  default long countDocuments(ClientSession clientSession, Bson bson) {
    return getLockChecker().invoke(() -> getImpl().countDocuments(clientSession, bson));
  }

  @Override
  default long countDocuments(ClientSession clientSession, Bson bson, CountOptions countOptions) {
    return getLockChecker().invoke(() -> getImpl().countDocuments(clientSession, bson, countOptions));
  }

  @Override
  default long estimatedDocumentCount() {
    return getLockChecker().invoke(() -> getImpl().estimatedDocumentCount());
  }

  @Override
  default long estimatedDocumentCount(EstimatedDocumentCountOptions estimatedDocumentCountOptions) {
    return getLockChecker().invoke(() -> getImpl().estimatedDocumentCount(estimatedDocumentCountOptions));
  }

  @Override
  default <TResult> DistinctIterable<TResult> distinct(ClientSession clientSession, String s, Class<TResult> aClass) {
    return null;
  }

  @Override
  default <TResult> DistinctIterable<TResult> distinct(ClientSession clientSession, String s, Bson bson, Class<TResult> aClass) {
    return null;
  }

  @Override
  default FindIterable<T> find(ClientSession clientSession) {
    return null;
  }

  @Override
  default <TResult> FindIterable<TResult> find(ClientSession clientSession, Class<TResult> aClass) {
    return null;
  }

  @Override
  default FindIterable<T> find(ClientSession clientSession, Bson bson) {
    return null;
  }

  @Override
  default <TResult> FindIterable<TResult> find(ClientSession clientSession, Bson bson, Class<TResult> aClass) {
    return null;
  }

  @Override
  default AggregateIterable<T> aggregate(ClientSession clientSession, List<? extends Bson> list) {
    return null;
  }

  @Override
  default <TResult> AggregateIterable<TResult> aggregate(ClientSession clientSession, List<? extends Bson> list, Class<TResult> aClass) {
    return null;
  }

  @Override
  default ChangeStreamIterable<T> watch() {
    return null;
  }

  @Override
  default <TResult> ChangeStreamIterable<TResult> watch(Class<TResult> aClass) {
    return null;
  }

  @Override
  default ChangeStreamIterable<T> watch(List<? extends Bson> list) {
    return null;
  }

  @Override
  default <TResult> ChangeStreamIterable<TResult> watch(List<? extends Bson> list, Class<TResult> aClass) {
    return null;
  }

  @Override
  default ChangeStreamIterable<T> watch(ClientSession clientSession) {
    return null;
  }

  @Override
  default <TResult> ChangeStreamIterable<TResult> watch(ClientSession clientSession, Class<TResult> aClass) {
    return null;
  }

  @Override
  default ChangeStreamIterable<T> watch(ClientSession clientSession, List<? extends Bson> list) {
    return null;
  }

  @Override
  default <TResult> ChangeStreamIterable<TResult> watch(ClientSession clientSession, List<? extends Bson> list, Class<TResult> aClass) {
    return null;
  }

  @Override
  default MapReduceIterable<T> mapReduce(ClientSession clientSession, String s, String s1) {
    return null;
  }

  @Override
  default <TResult> MapReduceIterable<TResult> mapReduce(ClientSession clientSession, String s, String s1, Class<TResult> aClass) {
    return null;
  }

  @Override
  default BulkWriteResult bulkWrite(ClientSession clientSession, List<? extends WriteModel<? extends T>> list) {
    return null;
  }

  @Override
  default BulkWriteResult bulkWrite(ClientSession clientSession, List<? extends WriteModel<? extends T>> list, BulkWriteOptions bulkWriteOptions) {
    return null;
  }

  @Override
  default void insertOne(ClientSession clientSession, T t) {

  }

  @Override
  default void insertOne(ClientSession clientSession, T t, InsertOneOptions insertOneOptions) {

  }

  @Override
  default void insertMany(ClientSession clientSession, List<? extends T> list) {

  }

  @Override
  default void insertMany(ClientSession clientSession, List<? extends T> list, InsertManyOptions insertManyOptions) {

  }

  @Override
  default DeleteResult deleteOne(ClientSession clientSession, Bson bson) {
    return null;
  }

  @Override
  default DeleteResult deleteOne(ClientSession clientSession, Bson bson, DeleteOptions deleteOptions) {
    return null;
  }

  @Override
  default DeleteResult deleteMany(ClientSession clientSession, Bson bson) {
    return null;
  }

  @Override
  default DeleteResult deleteMany(ClientSession clientSession, Bson bson, DeleteOptions deleteOptions) {
    return null;
  }

  @Override
  default UpdateResult replaceOne(Bson bson, T t, ReplaceOptions replaceOptions) {
    return null;
  }

  @Override
  default UpdateResult replaceOne(ClientSession clientSession, Bson bson, T t) {
    return null;
  }

  @Override
  default UpdateResult replaceOne(ClientSession clientSession, Bson bson, T t, UpdateOptions updateOptions) {
    return null;
  }

  @Override
  default UpdateResult replaceOne(ClientSession clientSession, Bson bson, T t, ReplaceOptions replaceOptions) {
    return null;
  }

  @Override
  default UpdateResult updateOne(ClientSession clientSession, Bson bson, Bson bson1) {
    return null;
  }

  @Override
  default UpdateResult updateOne(ClientSession clientSession, Bson bson, Bson bson1, UpdateOptions updateOptions) {
    return null;
  }

  @Override
  default UpdateResult updateOne(Bson bson, List<? extends Bson> list) {
    return null;
  }

  @Override
  default UpdateResult updateOne(Bson bson, List<? extends Bson> list, UpdateOptions updateOptions) {
    return null;
  }

  @Override
  default UpdateResult updateOne(ClientSession clientSession, Bson bson, List<? extends Bson> list) {
    return null;
  }

  @Override
  default UpdateResult updateOne(ClientSession clientSession, Bson bson, List<? extends Bson> list, UpdateOptions updateOptions) {
    return null;
  }

  @Override
  default UpdateResult updateMany(ClientSession clientSession, Bson bson, Bson bson1) {
    return null;
  }

  @Override
  default UpdateResult updateMany(ClientSession clientSession, Bson bson, Bson bson1, UpdateOptions updateOptions) {
    return null;
  }

  @Override
  default UpdateResult updateMany(Bson bson, List<? extends Bson> list) {
    return null;
  }

  @Override
  default UpdateResult updateMany(Bson bson, List<? extends Bson> list, UpdateOptions updateOptions) {
    return null;
  }

  @Override
  default UpdateResult updateMany(ClientSession clientSession, Bson bson, List<? extends Bson> list) {
    return null;
  }

  @Override
  default UpdateResult updateMany(ClientSession clientSession, Bson bson, List<? extends Bson> list, UpdateOptions updateOptions) {
    return null;
  }

  @Override
  default T findOneAndDelete(ClientSession clientSession, Bson bson) {
    return null;
  }

  @Override
  default T findOneAndDelete(ClientSession clientSession, Bson bson, FindOneAndDeleteOptions findOneAndDeleteOptions) {
    return null;
  }

  @Override
  default T findOneAndReplace(ClientSession clientSession, Bson bson, T t) {
    return null;
  }

  @Override
  default T findOneAndReplace(ClientSession clientSession, Bson bson, T t, FindOneAndReplaceOptions findOneAndReplaceOptions) {
    return null;
  }

  @Override
  default T findOneAndUpdate(ClientSession clientSession, Bson bson, Bson bson1) {
    return null;
  }

  @Override
  default T findOneAndUpdate(ClientSession clientSession, Bson bson, Bson bson1, FindOneAndUpdateOptions findOneAndUpdateOptions) {
    return null;
  }

  @Override
  default T findOneAndUpdate(Bson bson, List<? extends Bson> list) {
    return null;
  }

  @Override
  default T findOneAndUpdate(Bson bson, List<? extends Bson> list, FindOneAndUpdateOptions findOneAndUpdateOptions) {
    return null;
  }

  @Override
  default T findOneAndUpdate(ClientSession clientSession, Bson bson, List<? extends Bson> list) {
    return null;
  }

  @Override
  default T findOneAndUpdate(ClientSession clientSession, Bson bson, List<? extends Bson> list, FindOneAndUpdateOptions findOneAndUpdateOptions) {
    return null;
  }

  @Override
  default void drop(ClientSession clientSession) {

  }

  @Override
  default String createIndex(ClientSession clientSession, Bson bson) {
    return null;
  }

  @Override
  default String createIndex(ClientSession clientSession, Bson bson, IndexOptions indexOptions) {
    return null;
  }

  @Override
  default List<String> createIndexes(List<IndexModel> list, CreateIndexOptions createIndexOptions) {
    return null;
  }

  @Override
  default List<String> createIndexes(ClientSession clientSession, List<IndexModel> list) {
    return null;
  }

  @Override
  default List<String> createIndexes(ClientSession clientSession, List<IndexModel> list, CreateIndexOptions createIndexOptions) {
    return null;
  }

  @Override
  default ListIndexesIterable<Document> listIndexes(ClientSession clientSession) {
    return null;
  }

  @Override
  default <TResult> ListIndexesIterable<TResult> listIndexes(ClientSession clientSession, Class<TResult> aClass) {
    return null;
  }

  @Override
  default void dropIndex(String s, DropIndexOptions dropIndexOptions) {

  }

  @Override
  default void dropIndex(Bson bson, DropIndexOptions dropIndexOptions) {

  }

  @Override
  default void dropIndex(ClientSession clientSession, String s) {

  }

  @Override
  default void dropIndex(ClientSession clientSession, Bson bson) {

  }

  @Override
  default void dropIndex(ClientSession clientSession, String s, DropIndexOptions dropIndexOptions) {

  }

  @Override
  default void dropIndex(ClientSession clientSession, Bson bson, DropIndexOptions dropIndexOptions) {

  }

  @Override
  default void dropIndexes(ClientSession clientSession) {

  }

  @Override
  default void dropIndexes(DropIndexOptions dropIndexOptions) {

  }

  @Override
  default void dropIndexes(ClientSession clientSession, DropIndexOptions dropIndexOptions) {

  }

  @Override
  default void renameCollection(ClientSession clientSession, MongoNamespace mongoNamespace) {

  }

  @Override
  default void renameCollection(ClientSession clientSession, MongoNamespace mongoNamespace, RenameCollectionOptions renameCollectionOptions) {

  }
}
