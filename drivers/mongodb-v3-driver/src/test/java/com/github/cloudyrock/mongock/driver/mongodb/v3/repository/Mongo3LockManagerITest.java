package com.github.cloudyrock.mongock.driver.mongodb.v3.repository;

import com.github.cloudyrock.mongock.driver.mongodb.test.template.MongoLockManagerITestBase;
import com.github.cloudyrock.mongock.driver.mongodb.test.template.util.MongoDbDriverTestAdapter;
import com.github.cloudyrock.mongock.driver.mongodb.v3.MongoDb3DriverTestAdapterImpl;

public class Mongo3LockManagerITest extends MongoLockManagerITestBase {

  protected void initializeRepository() {
    repository = new Mongo3LockRepository(getDataBase().getCollection(LOCK_COLLECTION_NAME), true);
    repository.initialize();
  }

  @Override
  protected MongoDbDriverTestAdapter getAdapter(String collectionName) {
    return new MongoDb3DriverTestAdapterImpl(getDataBase().getCollection(collectionName));
  }
}
