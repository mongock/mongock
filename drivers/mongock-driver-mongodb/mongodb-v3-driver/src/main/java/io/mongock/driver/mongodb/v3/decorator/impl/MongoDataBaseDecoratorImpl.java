package io.mongock.driver.mongodb.v3.decorator.impl;

import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.mongock.driver.mongodb.v3.decorator.MongoDatabaseDecorator;
import com.mongodb.client.MongoDatabase;

public class MongoDataBaseDecoratorImpl implements MongoDatabaseDecorator {
  private final MongoDatabase impl;
  private final LockGuardInvoker invoker;

  public MongoDataBaseDecoratorImpl(MongoDatabase implementation, LockGuardInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.invoker = lockerCheckInvoker;
  }

  public MongoDatabase getImpl() {
    return impl;
  }

  public LockGuardInvoker getInvoker() {
    return invoker;
  }

}
