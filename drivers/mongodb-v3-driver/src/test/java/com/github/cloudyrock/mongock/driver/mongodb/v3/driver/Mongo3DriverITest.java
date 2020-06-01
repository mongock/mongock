package com.github.cloudyrock.mongock.driver.mongodb.v3.driver;


import com.github.cloudyrock.mongock.driver.mongodb.test.template.MongoDriverITestBase;
import com.github.cloudyrock.mongock.driver.mongodb.test.template.util.MongoDbDriverTestAdapter;
import com.github.cloudyrock.mongock.driver.mongodb.v3.MongoDb3DriverTestAdapterImpl;

public class Mongo3DriverITest extends MongoDriverITestBase {

  @Override
  protected MongoCore3Driver getDriver() {
    MongoCore3Driver driver = new MongoCore3Driver(this.getDataBase());
    driver.setChangeLogCollectionName(CHANGELOG_COLLECTION_NAME);
    return driver;
  }

  @Override
  protected MongoDbDriverTestAdapter getAdapter(String collectionName) {
    return new MongoDb3DriverTestAdapterImpl(getDataBase().getCollection(collectionName));
  }

}
