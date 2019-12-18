package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.NewMongoClientDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.mongodb.client.MongoClient;

public class NewMongoClientDecoratorImpl implements NewMongoClientDecorator {

  private final MongoClient impl;
  private final MethodInvoker invoker;

  public NewMongoClientDecoratorImpl(MongoClient mongoClientImpl, MethodInvoker lockerCheckInvoker) {
    this.impl = mongoClientImpl;
    this.invoker = lockerCheckInvoker;
  }

  @Override
  public MongoClient getImpl() {
    return this.impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return this.invoker;
  }
}
