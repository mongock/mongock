package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator;

import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.decorator.impl.MongoDataBaseDecoratorImpl;
import com.mongodb.ClientSessionOptions;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import io.changock.driver.api.lock.guard.decorator.Invokable;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.ClientSessionDecoratorImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongoDatabaseFactoryDecoratorImpl;
import io.changock.migration.api.annotations.NonLockGuarded;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.mongodb.MongoDatabaseFactory;

import java.util.Optional;

public interface MongoDatabaseFactoryDecorator extends Invokable, MongoDatabaseFactory {

  MongoDatabaseFactory getImpl();

  @Override
  default MongoDatabase getMongoDatabase() throws DataAccessException {
    return new MongoDataBaseDecoratorImpl(getImpl().getMongoDatabase(), getInvoker());
  }

  @Override
  default MongoDatabase getMongoDatabase(String dbName) throws DataAccessException {
    return new MongoDataBaseDecoratorImpl(getImpl().getMongoDatabase(dbName), getInvoker());
  }

  @Override
  @NonLockGuarded
  default PersistenceExceptionTranslator getExceptionTranslator() {
    return getImpl().getExceptionTranslator();
  }

  @Override
  @NonLockGuarded
  default CodecRegistry getCodecRegistry() {
    return getImpl().getCodecRegistry();
  }

  @Override
  @NonLockGuarded
  default boolean hasCodecFor(Class<?> type) {
    return getImpl().hasCodecFor(type);

  }

  @Override
  @NonLockGuarded
  default <T> Optional<Codec<T>> getCodecFor(Class<T> type) {
    return getImpl().getCodecFor(type);
  }

  @Override
  default ClientSession getSession(ClientSessionOptions options) {
    return new ClientSessionDecoratorImpl(getInvoker().invoke(() -> getImpl().getSession(options)), getInvoker());
  }

  @Override
  default MongoDatabaseFactory withSession(ClientSessionOptions options) {
    return new MongoDatabaseFactoryDecoratorImpl(getInvoker().invoke(() -> getImpl().withSession(options)), getInvoker());
  }

  @Override
  default MongoDatabaseFactory withSession(ClientSession session) {
    return new MongoDatabaseFactoryDecoratorImpl(getInvoker().invoke(() -> getImpl().withSession(session)), getInvoker());
  }

  @Override
  @NonLockGuarded
  default boolean isTransactionActive() {
    return getImpl().isTransactionActive();
  }
}
