package com.github.cloudyrock.mongock.driver.mongodb.springdata.v3;


import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.SpringDataMongo3Driver;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.driver.SpringData3DriverTestAdapterImpl;
import com.github.cloudyrock.mongock.driver.mongodb.test.template.MongoDriverITestBase;
import com.github.cloudyrock.mongock.driver.mongodb.test.template.util.MongoDbDriverTestAdapter;
import org.springframework.data.mongodb.core.MongoTemplate;

public class SpringDataMongo3DriverITest extends MongoDriverITestBase {

  @Override
  protected SpringDataMongo3Driver getDriver() {
    SpringDataMongo3Driver driver = new SpringDataMongo3Driver(getMongoTemplate());
    driver.setChangeLogCollectionName(CHANGELOG_COLLECTION_NAME);
    return driver;
  }

  @Override
  protected MongoDbDriverTestAdapter getAdapter(String collectionName) {
    return new SpringData3DriverTestAdapterImpl(getDataBase().getCollection(collectionName));
  }

  private MongoTemplate getMongoTemplate() {
    return new MongoTemplate(this.getMongoClient(), this.getDataBase().getName());
  }
}
