package io.mongock.driver.mongodb.springdata.v3.decorator.impl;

import io.mongock.driver.api.lock.guard.decorator.DecoratorBase;
import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.mongock.driver.mongodb.springdata.v3.decorator.MongoDatabaseFactoryDecorator;
import org.springframework.data.mongodb.MongoDatabaseFactory;

public class MongoDatabaseFactoryDecoratorImpl extends DecoratorBase<MongoDatabaseFactory> implements MongoDatabaseFactoryDecorator {

  public MongoDatabaseFactoryDecoratorImpl(MongoDatabaseFactory impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
