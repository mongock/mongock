package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.github.cloudyrock.mongock.decorator.MongoDatabaseDecorator;
import com.mongodb.client.MongoDatabase;

public class MongoDataBaseDecoratorImpl implements MongoDatabaseDecorator {
  private final MongoDatabase impl;
  private final MethodInvoker invoker;

  public MongoDataBaseDecoratorImpl(MongoDatabase implementation, MethodInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.invoker = lockerCheckInvoker;
  }

  public MongoDatabase getImpl() {
    return impl;
  }

  public MethodInvoker getInvoker() {
    return invoker;
  }
}
