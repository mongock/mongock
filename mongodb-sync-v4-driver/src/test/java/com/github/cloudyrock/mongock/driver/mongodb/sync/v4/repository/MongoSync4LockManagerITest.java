package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.repository;

import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.MongoDbSync4DriverTestAdapterImpl;
import com.github.cloudyrock.mongock.driver.mongodb.test.template.MongoLockManagerITestBase;
import com.github.cloudyrock.mongock.driver.mongodb.test.template.util.MongoDBDriverTestAdapter;

public class MongoSync4LockManagerITest extends MongoLockManagerITestBase {

  protected void initializeRepository() {
    repository = new MongoSync4LockRepository(getDataBase().getCollection(LOCK_COLLECTION_NAME), true);
    repository.initialize();
  }

  @Override
  protected MongoDBDriverTestAdapter getAdapter(String collectionName) {
    return new MongoDbSync4DriverTestAdapterImpl(getDataBase().getCollection(collectionName));
  }
}
