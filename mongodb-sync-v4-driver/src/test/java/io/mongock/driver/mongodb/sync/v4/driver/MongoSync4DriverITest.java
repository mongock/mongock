package io.mongock.driver.mongodb.sync.v4.driver;


import io.mongock.driver.mongodb.sync.v4.MongoDbSync4DriverTestAdapterImpl;
import io.mongock.driver.mongodb.test.template.MongoDriverITestBase;
import io.mongock.driver.mongodb.test.template.util.MongoDBDriverTestAdapter;

public class MongoSync4DriverITest extends MongoDriverITestBase {

  @Override
  protected MongoSync4Driver getDriverWithTransactionDisabled() {
    MongoSync4Driver driver = MongoSync4Driver.withDefaultLock(this.getMongoClient(), DEFAULT_DATABASE_NAME);
    driver.disableTransaction();
    driver.setChangeLogRepositoryName(CHANGELOG_COLLECTION_NAME);
    return driver;
  }

  @Override
  protected MongoDBDriverTestAdapter getAdapter(String collectionName) {
    return new MongoDbSync4DriverTestAdapterImpl(getDataBase().getCollection(collectionName));
  }
}
