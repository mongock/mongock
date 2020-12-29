package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.impl.ClientSessionDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.impl.MongoDbFactoryDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.v3.decorator.impl.MongoDataBaseDecoratorImpl;
import com.mongodb.ClientSessionOptions;
import com.mongodb.DB;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import com.github.cloudyrock.mongock.driver.api.lock.guard.decorator.Invokable;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.mongodb.MongoDbFactory;

@Deprecated
public interface MongoDbFactoryDecorator extends Invokable, MongoDbFactory {

  MongoDbFactory getImpl();

  @Override
  default MongoDatabase getDb() throws DataAccessException {
    return new MongoDataBaseDecoratorImpl(getImpl().getDb(), getInvoker());
  }

  @Override
  default MongoDatabase getDb(String dbName) throws DataAccessException {
    return new MongoDataBaseDecoratorImpl(getImpl().getDb(dbName), getInvoker());
  }

  @Override
  default PersistenceExceptionTranslator getExceptionTranslator() {
    return getImpl().getExceptionTranslator();
  }

  @Override
  default DB getLegacyDb() {
    throw new UnsupportedOperationException("Removed DB support from Mongock due to deprecated API. Please use MongoDatabase instead");
  }


  @Override
  default ClientSession getSession(ClientSessionOptions clientSessionOptions) {
    return new ClientSessionDecoratorImpl(getInvoker().invoke(() -> getImpl().getSession(clientSessionOptions)), getInvoker());
  }

  @Override
  default MongoDbFactory withSession(ClientSession clientSession) {
    return new MongoDbFactoryDecoratorImpl(getInvoker().invoke(() -> getImpl().withSession(clientSession)), getInvoker());
  }
}
