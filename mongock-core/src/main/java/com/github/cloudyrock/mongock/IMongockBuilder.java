package com.github.cloudyrock.mongock;

import com.mongodb.DB;
import com.mongodb.client.MongoDatabase;

public interface IMongockBuilder {
  IMongock build();
  IMongockBuilder setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock);
  IMongock constructMongock(ChangeEntryRepository changeEntryRepository, ChangeService changeService, LockChecker lockChecker,
      MongoDatabase mongoDatabaseProxy, DB db, ProxyFactory proxyFactory);
}
