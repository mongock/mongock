package com.github.cloudyrock.mongock;

import org.springframework.beans.factory.InitializingBean;

import java.io.Closeable;

public class SpringMongock extends Mongock implements InitializingBean {

  SpringMongock(ChangeEntryRepository changeEntryRepository, Closeable mongoClientCloseable, ChangeService changeService, LockChecker lockChecker) {
    super(changeEntryRepository, mongoClientCloseable, changeService, lockChecker);
  }

  /**
   * For Spring users: executing mongock after bean is created in the Spring context
   */
  @Override
  public void afterPropertiesSet() {
    execute();
  }
}
