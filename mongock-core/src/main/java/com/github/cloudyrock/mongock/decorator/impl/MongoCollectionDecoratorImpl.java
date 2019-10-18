package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.github.cloudyrock.mongock.decorator.MongoCollectionDecorator;
import com.mongodb.client.MongoCollection;

public class MongoCollectionDecoratorImpl<T> implements MongoCollectionDecorator<T> {

  private final MongoCollection<T> impl;
  private final MethodInvoker lockChecker;

  public MongoCollectionDecoratorImpl(MongoCollection<T> implementation, MethodInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.lockChecker = lockerCheckInvoker;
  }

  @Override
  public MongoCollection<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return lockChecker;
  }

}
