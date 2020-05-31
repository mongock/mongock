package com.github.cloudyrock.mongock.driver.mongodb.v3.driver;


public class Mongo3DriverITest extends MongoDriverITestBase {

  @Override
  protected MongoCore3Driver getDriver() {
    MongoCore3Driver driver = new MongoCore3Driver(this.getDataBase());
    driver.setChangeLogCollectionName(CHANGELOG_COLLECTION_NAME);
    return driver;
  }

}
