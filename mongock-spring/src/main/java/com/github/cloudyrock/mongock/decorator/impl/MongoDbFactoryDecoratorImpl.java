package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.MongoDbFactoryDecorator;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import org.springframework.data.mongodb.MongoDbFactory;

public class MongoDbFactoryDecoratorImpl implements MongoDbFactoryDecorator {

  private final MethodInvoker invoker;
  private final MongoDbFactory impl;

  public MongoDbFactoryDecoratorImpl(MongoDbFactory implementation, MethodInvoker invoker) {
    this.impl = implementation;
    this.invoker = invoker;
  }

  @Override
  public MongoDbFactory getImpl() {
    return null;
  }

  @Override
  public MethodInvoker getInvoker() {
    return null;
  }
}
