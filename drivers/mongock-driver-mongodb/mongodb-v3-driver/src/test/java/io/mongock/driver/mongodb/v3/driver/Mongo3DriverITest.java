package io.mongock.driver.mongodb.v3.driver;


import io.mongock.driver.mongodb.test.template.MongoDriverITestBase;
import io.mongock.driver.mongodb.test.template.util.MongoDBDriverTestAdapter;
import io.mongock.driver.mongodb.v3.MongoDb3DriverTestAdapterImpl;

public class Mongo3DriverITest extends MongoDriverITestBase {

  @Override
  protected MongoCore3Driver getDriverWithTransactionDisabled() {
    MongoCore3Driver driver = MongoCore3Driver.withDefaultLock(this.getMongoClient(), DEFAULT_DATABASE_NAME);
    driver.setMigrationRepositoryName(CHANGELOG_COLLECTION_NAME);
    driver.disableTransaction();
    return driver;
  }

  @Override
  protected MongoDBDriverTestAdapter getAdapter(String collectionName) {
    return new MongoDb3DriverTestAdapterImpl(getDataBase().getCollection(collectionName));
  }

}
