package com.github.cloudyrock.mongock.driver.mongodb.v3.repository;

public class Mongo3LockManagerITest extends MongoLockManagerITestBase {

  protected void initializeRepository() {
    repository = new Mongo3LockRepository(collection, true);
    repository.initialize();
  }
}
