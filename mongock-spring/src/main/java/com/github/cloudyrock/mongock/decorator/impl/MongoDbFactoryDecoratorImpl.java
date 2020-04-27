package com.github.cloudyrock.mongock.decorator.impl;

import com.github.cloudyrock.mongock.decorator.util.DecoratorBase;
import com.github.cloudyrock.mongock.decorator.util.MethodInvoker;
import com.github.cloudyrock.mongock.decorator.MongoDbFactoryDecorator;
import org.springframework.data.mongodb.MongoDbFactory;

@Deprecated
public class MongoDbFactoryDecoratorImpl extends DecoratorBase<MongoDbFactory> implements MongoDbFactoryDecorator {

  public MongoDbFactoryDecoratorImpl(MongoDbFactory impl, MethodInvoker invoker) {
    super(impl, invoker);
  }

}
