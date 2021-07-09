package com.github.cloudyrock.mongock.driver.mongodb.v3.repository;

import com.github.cloudyrock.mongock.driver.mongodb.test.template.MongoLockManagerITestBase;
import com.github.cloudyrock.mongock.driver.mongodb.test.template.util.MongoDBDriverTestAdapter;
import com.github.cloudyrock.mongock.driver.mongodb.v3.MongoDb3DriverTestAdapterImpl;

public class Mongo3LockManagerITest extends MongoLockManagerITestBase {

  protected void initializeRepository() {
    repository = new Mongo3LockRepository(getDataBase().getCollection(LOCK_COLLECTION_NAME), true);
    repository.setIndexCreation(true);
    repository.initialize();
  }

  @Override
  protected MongoDBDriverTestAdapter getAdapter(String collectionName) {
    return new MongoDb3DriverTestAdapterImpl(getDataBase().getCollection(collectionName));
  }
}
