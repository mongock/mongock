package com.github.cloudyrock.mongock.decorator;

import com.github.cloudyrock.mongock.decorator.impl.MongoDataBaseDecoratorImpl;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.mongodb.ClientSessionOptions;
import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.ClientSession;
import com.mongodb.client.ListDatabasesIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.connection.ClusterDescription;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;

public interface NewMongoClientDecorator extends MongoClient {

  MongoClient getImpl();

  MethodInvoker getInvoker();

  @Override
  default MongoDatabase getDatabase(String s) {
    return new MongoDataBaseDecoratorImpl(getImpl().getDatabase(s), getInvoker());
  }

  @Override
  default ClientSession startSession() {
    return getInvoker().invoke(() -> getImpl().startSession());
  }

  @Override
  default ClientSession startSession(ClientSessionOptions clientSessionOptions) {
    return getInvoker().invoke(() -> getImpl().startSession(clientSessionOptions));
  }

  @Override
  default void close() {
    getInvoker().invoke(()-> getImpl().close());
  }

  @Override
  default MongoIterable<String> listDatabaseNames() {
    return getInvoker().invoke(()-> getImpl().listDatabaseNames());
  }

  @Override
  default MongoIterable<String> listDatabaseNames(ClientSession clientSession) {
    return getInvoker().invoke(()-> getImpl().listDatabaseNames(clientSession));
  }

  @Override
  default ListDatabasesIterable<Document> listDatabases() {
    return getInvoker().invoke(()-> getImpl().listDatabases());
  }

  @Override
  default ListDatabasesIterable<Document> listDatabases(ClientSession clientSession) {
    return getInvoker().invoke(()-> getImpl().listDatabases(clientSession));
  }

  @Override
  default <TResult> ListDatabasesIterable<TResult> listDatabases(Class<TResult> aClass) {
    return getInvoker().invoke(()-> getImpl().listDatabases(aClass));
  }

  @Override
  default <TResult> ListDatabasesIterable<TResult> listDatabases(ClientSession clientSession, Class<TResult> aClass) {
    return getInvoker().invoke(()-> getImpl().listDatabases(clientSession, aClass));
  }

  @Override
  default ChangeStreamIterable<Document> watch() {
    return getInvoker().invoke(()-> getImpl().watch());
  }

  @Override
  default <TResult> ChangeStreamIterable<TResult> watch(Class<TResult> aClass) {
    return getInvoker().invoke(()-> getImpl().watch(aClass));
  }

  @Override
  default ChangeStreamIterable<Document> watch(List<? extends Bson> list) {
    return getInvoker().invoke(()-> getImpl().watch(list));
  }

  @Override
  default <TResult> ChangeStreamIterable<TResult> watch(List<? extends Bson> list, Class<TResult> aClass) {
    return getInvoker().invoke(()-> getImpl().watch(list, aClass));
  }

  @Override
  default ChangeStreamIterable<Document> watch(ClientSession clientSession) {
    return getInvoker().invoke(()-> getImpl().watch(clientSession));
  }

  @Override
  default <TResult> ChangeStreamIterable<TResult> watch(ClientSession clientSession, Class<TResult> aClass) {
    return getInvoker().invoke(()-> getImpl().watch(clientSession, aClass));
  }

  @Override
  default ChangeStreamIterable<Document> watch(ClientSession clientSession, List<? extends Bson> list) {
    return getInvoker().invoke(()-> getImpl().watch(clientSession, list));
  }

  @Override
  default <TResult> ChangeStreamIterable<TResult> watch(ClientSession clientSession, List<? extends Bson> list, Class<TResult> aClass) {
    return getInvoker().invoke(()-> getImpl().watch(clientSession, list,aClass));
  }

  @Override
  default ClusterDescription getClusterDescription() {
    return getInvoker().invoke(()-> getImpl().getClusterDescription());
  }
}
