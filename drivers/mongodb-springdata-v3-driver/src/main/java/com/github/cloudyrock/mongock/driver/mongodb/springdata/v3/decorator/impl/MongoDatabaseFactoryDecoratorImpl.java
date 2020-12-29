package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.MongoDatabaseFactoryDecorator;
import com.github.cloudyrock.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.MongoDatabaseFactory;

public class MongoDatabaseFactoryDecoratorImpl extends DecoratorBase<MongoDatabaseFactory> implements MongoDatabaseFactoryDecorator {

  public MongoDatabaseFactoryDecoratorImpl(MongoDatabaseFactory impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
