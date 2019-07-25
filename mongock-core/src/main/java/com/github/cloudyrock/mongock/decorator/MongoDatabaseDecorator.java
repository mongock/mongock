package com.github.cloudyrock.mongock.decorator;

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


public class MongoDatabaseDecorator implements MongoDatabase {
  private final MongoDatabase impl;
  private final LockCheckInvoker invoker;

  public MongoDatabaseDecorator(MongoDatabase implementation, LockCheckInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.invoker = lockerCheckInvoker;
  }

  private LockCheckInvoker getInvoker() {
    return invoker;
  }

  @Override
  public String getName() {
    return impl.getName();
  }

  @Override
  public CodecRegistry getCodecRegistry() {
    return impl.getCodecRegistry();
  }

  @Override
  public ReadPreference getReadPreference() {
    return impl.getReadPreference();
  }

  @Override
  public WriteConcern getWriteConcern() {
    return impl.getWriteConcern();
  }

  @Override
  public ReadConcern getReadConcern() {
    return impl.getReadConcern();
  }

  @Override
  public MongoDatabase withCodecRegistry(CodecRegistry codecRegistry) {
    return new MongoDatabaseDecorator(impl.withCodecRegistry(codecRegistry), getInvoker());
  }

  @Override
  public MongoDatabase withReadPreference(ReadPreference readPreference) {
    return new MongoDatabaseDecorator(impl.withReadPreference(readPreference), getInvoker());
  }

  @Override
  public MongoDatabase withWriteConcern(WriteConcern writeConcern) {
    return new MongoDatabaseDecorator(impl.withWriteConcern(writeConcern), getInvoker());
  }

  @Override
  public MongoDatabase withReadConcern(ReadConcern readConcern) {
    return new MongoDatabaseDecorator(impl.withReadConcern(readConcern), getInvoker());
  }

  @Override
//  @SuppressWarnings("unchecked")
  public MongoCollection<Document> getCollection(String s) {
    return new MongoCollectionDecorator<>(impl.getCollection(s), getInvoker());
  }

  @Override
  public <TDocument> MongoCollection<TDocument> getCollection(String s, Class<TDocument> aClass) {
    return new MongoCollectionDecorator<>(impl.getCollection(s, aClass), getInvoker());
  }

  @Override
  public Document runCommand(Bson bson) {
    return invoker.invoke(() -> impl.runCommand(bson));
  }

  @Override
  public Document runCommand(Bson bson, ReadPreference readPreference) {
    return invoker.invoke(() -> impl.runCommand(bson, readPreference));
  }

  @Override
  public <TResult> TResult runCommand(Bson bson, Class<TResult> aClass) {
    return invoker.invoke(() -> impl.runCommand(bson, aClass));
  }

  @Override
  public <TResult> TResult runCommand(Bson bson, ReadPreference readPreference, Class<TResult> aClass) {
    return invoker.invoke(() -> impl.runCommand(bson, readPreference, aClass));
  }

  @Override
  public void drop() {
    invoker.invoke(impl::drop);
  }

  @Override
  public MongoIterable<String> listCollectionNames() {
    return new MongoIterableDecorator<>(impl.listCollectionNames(), getInvoker());
  }

  @Override
  public ListCollectionsIterable<Document> listCollections() {
    return new ListCollectionsIterableDecorator<>(impl.listCollections(), getInvoker());
  }

  @Override
  public <TResult> ListCollectionsIterable<TResult> listCollections(Class<TResult> aClass) {
    return new ListCollectionsIterableDecorator<>(impl.listCollections(aClass), invoker);
  }

  @Override
  public void createCollection(String s) {
    invoker.invoke(() -> impl.createCollection(s));
  }

  @Override
  public void createCollection(String s, CreateCollectionOptions createCollectionOptions) {
    invoker.invoke(() -> impl.createCollection(s, createCollectionOptions));
  }

  @Override
  public void createView(String s, String s1, List<? extends Bson> list) {
    invoker.invoke(() -> impl.createView(s, s1, list));
  }

  @Override
  public void createView(String s, String s1, List<? extends Bson> list, CreateViewOptions createViewOptions) {
    invoker.invoke(() -> impl.createView(s, s1, list, createViewOptions));
  }
}
