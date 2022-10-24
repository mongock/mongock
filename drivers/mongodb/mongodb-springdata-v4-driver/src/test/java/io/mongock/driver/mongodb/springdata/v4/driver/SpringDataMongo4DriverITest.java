package io.mongock.driver.mongodb.springdata.v4.driver;


import io.mongock.driver.mongodb.springdata.v4.*;
import io.mongock.driver.mongodb.test.template.MongoDriverITestBase;
import io.mongock.driver.mongodb.test.template.util.MongoDBDriverTestAdapter;
import org.springframework.data.mongodb.core.MongoTemplate;

public class SpringDataMongo4DriverITest extends MongoDriverITestBase {

  @Override
  protected SpringDataMongoV4Driver getDriverWithTransactionDisabled() {
    SpringDataMongoV4Driver driver = SpringDataMongoV4Driver.withDefaultLock(getMongoTemplate());
    driver.setChangeLogRepositoryName(CHANGELOG_COLLECTION_NAME);
    return driver;
  }

  @Override
  protected MongoDBDriverTestAdapter getAdapter(String collectionName) {
    return new SpringData4DriverTestAdapterImpl(getDataBase().getCollection(collectionName));
  }

  private MongoTemplate getMongoTemplate() {
    return new MongoTemplate(this.getMongoClient(), this.getDataBase().getName());
  }
}
