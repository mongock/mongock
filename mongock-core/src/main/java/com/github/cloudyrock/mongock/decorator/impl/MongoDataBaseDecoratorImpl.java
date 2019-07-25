package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.util.LockCheckInvoker;
import com.github.cloudyrock.mongock.decorator.MongoDatabaseDecorator;
import com.mongodb.client.MongoDatabase;

public class MongoDataBaseDecoratorImpl implements MongoDatabaseDecorator {
  private final MongoDatabase impl;
  private final LockCheckInvoker invoker;

  public MongoDataBaseDecoratorImpl(MongoDatabase implementation, LockCheckInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.invoker = lockerCheckInvoker;
  }

  public MongoDatabase getImpl() {
    return impl;
  }

  public LockCheckInvoker getInvoker() {
    return invoker;
  }
}
