package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.impl;

import io.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.decorator.MongoDbFactoryDecorator;
import org.springframework.data.mongodb.MongoDbFactory;

@Deprecated
public class MongoDbFactoryDecoratorImpl extends DecoratorBase<MongoDbFactory> implements MongoDbFactoryDecorator {

  public MongoDbFactoryDecoratorImpl(MongoDbFactory impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }

}
