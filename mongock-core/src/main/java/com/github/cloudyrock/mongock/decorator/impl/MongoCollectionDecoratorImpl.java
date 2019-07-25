package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.util.LockCheckInvoker;
import com.github.cloudyrock.mongock.decorator.MongoCollectionDecorator;
import com.mongodb.client.MongoCollection;

public class MongoCollectionDecoratorImpl<T> implements MongoCollectionDecorator<T> {


  private final MongoCollection<T> impl;
  private final LockCheckInvoker checker;


  public MongoCollectionDecoratorImpl(MongoCollection<T> implementation, LockCheckInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public MongoCollection<T> getImpl() {
    return impl;
  }

  @Override
  public LockCheckInvoker getInvoker() {
    return checker;
  }
}
