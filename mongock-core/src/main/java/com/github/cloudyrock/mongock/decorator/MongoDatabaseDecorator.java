package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.decorator.impl.ListCollectionsIterableDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.impl.MongoCollectionDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.impl.MongoDataBaseDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.impl.MongoIterableDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.util.LockCheckInvoker;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.ListCollectionsIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.CreateViewOptions;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import java.util.List;


public  interface MongoDatabaseDecorator extends MongoDatabase {

  MongoDatabase getImpl();
  LockCheckInvoker getInvoker();

  @Override
  default  String getName() {
    return getImpl().getName();
  }

  @Override
  default  CodecRegistry getCodecRegistry() {
    return getImpl().getCodecRegistry();
  }

  @Override
  default  ReadPreference getReadPreference() {
    return getImpl().getReadPreference();
  }

  @Override
  default  WriteConcern getWriteConcern() {
    return getImpl().getWriteConcern();
  }

  @Override
  default  ReadConcern getReadConcern() {
    return getImpl().getReadConcern();
  }

  @Override
  default  MongoDatabase withCodecRegistry(CodecRegistry codecRegistry) {
    return new MongoDataBaseDecoratorImpl(getImpl().withCodecRegistry(codecRegistry), getInvoker());
  }

  @Override
  default  MongoDatabase withReadPreference(ReadPreference readPreference) {
    return new MongoDataBaseDecoratorImpl(getImpl().withReadPreference(readPreference), getInvoker());
  }

  @Override
  default  MongoDatabase withWriteConcern(WriteConcern writeConcern) {
    return new MongoDataBaseDecoratorImpl(getImpl().withWriteConcern(writeConcern), getInvoker());
  }

  @Override
  default  MongoDatabase withReadConcern(ReadConcern readConcern) {
    return new MongoDataBaseDecoratorImpl(getImpl().withReadConcern(readConcern), getInvoker());
  }

  @Override
//  @SuppressWarnings("unchecked")
  default  MongoCollection<Document> getCollection(String s) {
    return new MongoCollectionDecoratorImpl<>(getImpl().getCollection(s), getInvoker());
  }

  @Override
  default  <TDocument> MongoCollection<TDocument> getCollection(String s, Class<TDocument> aClass) {
    return new MongoCollectionDecoratorImpl<>(getImpl().getCollection(s, aClass), getInvoker());
  }

  @Override
  default  Document runCommand(Bson bson) {
    return getInvoker().invoke(() -> getImpl().runCommand(bson));
  }

  @Override
  default  Document runCommand(Bson bson, ReadPreference readPreference) {
    return getInvoker().invoke(() -> getImpl().runCommand(bson, readPreference));
  }

  @Override
  default  <TResult> TResult runCommand(Bson bson, Class<TResult> aClass) {
    return getInvoker().invoke(() -> getImpl().runCommand(bson, aClass));
  }

  @Override
  default  <TResult> TResult runCommand(Bson bson, ReadPreference readPreference, Class<TResult> aClass) {
    return getInvoker().invoke(() -> getImpl().runCommand(bson, readPreference, aClass));
  }

  @Override
  default  void drop() {
    getInvoker().invoke(getImpl()::drop);
  }

  @Override
  default  MongoIterable<String> listCollectionNames() {
    return new MongoIterableDecoratorImpl<>(getImpl().listCollectionNames(), getInvoker());
  }

  @Override
  default  ListCollectionsIterable<Document> listCollections() {
    return new ListCollectionsIterableDecoratorImpl<>(getImpl().listCollections(), getInvoker());
  }

  @Override
  default  <TResult> ListCollectionsIterable<TResult> listCollections(Class<TResult> aClass) {
    return new ListCollectionsIterableDecoratorImpl<>(getImpl().listCollections(aClass), getInvoker());
  }

  @Override
  default  void createCollection(String s) {
    getInvoker().invoke(() -> getImpl().createCollection(s));
  }

  @Override
  default  void createCollection(String s, CreateCollectionOptions createCollectionOptions) {
    getInvoker().invoke(() -> getImpl().createCollection(s, createCollectionOptions));
  }

  @Override
  default  void createView(String s, String s1, List<? extends Bson> list) {
    getInvoker().invoke(() -> getImpl().createView(s, s1, list));
  }

  @Override
  default  void createView(String s, String s1, List<? extends Bson> list, CreateViewOptions createViewOptions) {
    getInvoker().invoke(() -> getImpl().createView(s, s1, list, createViewOptions));
  }
}
