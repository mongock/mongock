package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator;

import com.mongodb.ClientSessionOptions;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import io.changock.driver.api.lock.guard.decorator.Invokable;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.ClientSessionDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongoDatabaseFactoryDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.decorator.impl.MongoDataBaseDecoratorImpl;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.mongodb.MongoDatabaseFactory;
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
  default MongoDatabase getMongoDatabase() throws DataAccessException {
    return new MongoDataBaseDecoratorImpl(getImpl().getMongoDatabase(), getInvoker());
  }

  @Override
  default MongoDatabase getMongoDatabase(String dbName) throws DataAccessException {
    return new MongoDataBaseDecoratorImpl(getImpl().getMongoDatabase(dbName), getInvoker());
  }

  @Override
  default PersistenceExceptionTranslator getExceptionTranslator() {
    return getImpl().getExceptionTranslator();
  }


  @Override
  default ClientSession getSession(ClientSessionOptions clientSessionOptions) {
    return new ClientSessionDecoratorImpl(getInvoker().invoke(() -> getImpl().getSession(clientSessionOptions)), getInvoker());
  }

  @Override
  default MongoDatabaseFactory withSession(ClientSession clientSession) {
    return new MongoDatabaseFactoryDecoratorImpl(getInvoker().invoke(() -> getImpl().withSession(clientSession)), getInvoker());
  }
}
