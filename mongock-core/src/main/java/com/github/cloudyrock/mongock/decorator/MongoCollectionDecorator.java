package com.github.cloudyrock.mongock.decorator;

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

public class MongoCollectionDecorator<T> implements MongoCollection<T> {

  private final MongoCollection<T> impl;
  private final LockCheckInvoker checker;


  public MongoCollectionDecorator(MongoCollection<T> implementation, LockCheckInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }
  
  @Override
  public MongoNamespace getNamespace() {
    return null;
  }

  @Override
  public Class<T> getDocumentClass() {
    return null;
  }

  @Override
  public CodecRegistry getCodecRegistry() {
    return null;
  }

  @Override
  public ReadPreference getReadPreference() {
    return null;
  }

  @Override
  public WriteConcern getWriteConcern() {
    return null;
  }

  @Override
  public ReadConcern getReadConcern() {
    return null;
  }

  @Override
  public <NewTDocument> MongoCollection<NewTDocument> withDocumentClass(Class<NewTDocument> aClass) {
    return null;
  }

  @Override
  public MongoCollection<T> withCodecRegistry(CodecRegistry codecRegistry) {
    return null;
  }

  @Override
  public MongoCollection<T> withReadPreference(ReadPreference readPreference) {
    return null;
  }

  @Override
  public MongoCollection<T> withWriteConcern(WriteConcern writeConcern) {
    return null;
  }

  @Override
  public MongoCollection<T> withReadConcern(ReadConcern readConcern) {
    return null;
  }

  @Override
  public long count() {
    return 0;
  }

  @Override
  public long count(Bson bson) {
    return 0;
  }

  @Override
  public long count(Bson bson, CountOptions countOptions) {
    return 0;
  }

  @Override
  public <TResult> DistinctIterable<TResult> distinct(String s, Class<TResult> aClass) {
    return null;
  }

  @Override
  public <TResult> DistinctIterable<TResult> distinct(String s, Bson bson, Class<TResult> aClass) {
    return null;
  }

  @Override
  public FindIterable<T> find() {
    return null;
  }

  @Override
  public <TResult> FindIterable<TResult> find(Class<TResult> aClass) {
    return null;
  }

  @Override
  public FindIterable<T> find(Bson bson) {
    return null;
  }

  @Override
  public <TResult> FindIterable<TResult> find(Bson bson, Class<TResult> aClass) {
    return null;
  }

  @Override
  public AggregateIterable<T> aggregate(List<? extends Bson> list) {
    return null;
  }

  @Override
  public <TResult> AggregateIterable<TResult> aggregate(List<? extends Bson> list, Class<TResult> aClass) {
    return null;
  }

  @Override
  public MapReduceIterable<T> mapReduce(String s, String s1) {
    return null;
  }

  @Override
  public <TResult> MapReduceIterable<TResult> mapReduce(String s, String s1, Class<TResult> aClass) {
    return null;
  }

  @Override
  public BulkWriteResult bulkWrite(List<? extends WriteModel<? extends T>> list) {
    return null;
  }

  @Override
  public BulkWriteResult bulkWrite(List<? extends WriteModel<? extends T>> list, BulkWriteOptions bulkWriteOptions) {
    return null;
  }

  @Override
  public void insertOne(T t) {

  }

  @Override
  public void insertOne(T t, InsertOneOptions insertOneOptions) {

  }

  @Override
  public void insertMany(List<? extends T> list) {

  }

  @Override
  public void insertMany(List<? extends T> list, InsertManyOptions insertManyOptions) {

  }

  @Override
  public DeleteResult deleteOne(Bson bson) {
    return null;
  }

  @Override
  public DeleteResult deleteOne(Bson bson, DeleteOptions deleteOptions) {
    return null;
  }

  @Override
  public DeleteResult deleteMany(Bson bson) {
    return null;
  }

  @Override
  public DeleteResult deleteMany(Bson bson, DeleteOptions deleteOptions) {
    return null;
  }

  @Override
  public UpdateResult replaceOne(Bson bson, T t) {
    return null;
  }

  @Override
  public UpdateResult replaceOne(Bson bson, T t, UpdateOptions updateOptions) {
    return null;
  }

  @Override
  public UpdateResult updateOne(Bson bson, Bson bson1) {
    return null;
  }

  @Override
  public UpdateResult updateOne(Bson bson, Bson bson1, UpdateOptions updateOptions) {
    return null;
  }

  @Override
  public UpdateResult updateMany(Bson bson, Bson bson1) {
    return null;
  }

  @Override
  public UpdateResult updateMany(Bson bson, Bson bson1, UpdateOptions updateOptions) {
    return null;
  }

  @Override
  public T findOneAndDelete(Bson bson) {
    return null;
  }

  @Override
  public T findOneAndDelete(Bson bson, FindOneAndDeleteOptions findOneAndDeleteOptions) {
    return null;
  }

  @Override
  public T findOneAndReplace(Bson bson, T t) {
    return null;
  }

  @Override
  public T findOneAndReplace(Bson bson, T t, FindOneAndReplaceOptions findOneAndReplaceOptions) {
    return null;
  }

  @Override
  public T findOneAndUpdate(Bson bson, Bson bson1) {
    return null;
  }

  @Override
  public T findOneAndUpdate(Bson bson, Bson bson1, FindOneAndUpdateOptions findOneAndUpdateOptions) {
    return null;
  }

  @Override
  public void drop() {

  }

  @Override
  public String createIndex(Bson bson) {
    return null;
  }

  @Override
  public String createIndex(Bson bson, IndexOptions indexOptions) {
    return null;
  }

  @Override
  public List<String> createIndexes(List<IndexModel> list) {
    return null;
  }

  @Override
  public ListIndexesIterable<Document> listIndexes() {
    return null;
  }

  @Override
  public <TResult> ListIndexesIterable<TResult> listIndexes(Class<TResult> aClass) {
    return null;
  }

  @Override
  public void dropIndex(String s) {

  }

  @Override
  public void dropIndex(Bson bson) {

  }

  @Override
  public void dropIndexes() {

  }

  @Override
  public void renameCollection(MongoNamespace mongoNamespace) {

  }

  @Override
  public void renameCollection(MongoNamespace mongoNamespace, RenameCollectionOptions renameCollectionOptions) {

  }
}
