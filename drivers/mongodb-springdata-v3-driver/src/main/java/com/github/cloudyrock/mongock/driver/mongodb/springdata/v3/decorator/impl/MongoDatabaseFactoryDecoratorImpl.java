package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl;

import io.changock.driver.api.lock.guard.decorator.DecoratorBase;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.MongoDatabaseFactoryDecorator;
import org.springframework.data.mongodb.MongoDatabaseFactory;

public class MongoDatabaseFactoryDecoratorImpl extends DecoratorBase<MongoDatabaseFactory> implements MongoDatabaseFactoryDecorator {

  public MongoDatabaseFactoryDecoratorImpl(MongoDatabaseFactory impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
