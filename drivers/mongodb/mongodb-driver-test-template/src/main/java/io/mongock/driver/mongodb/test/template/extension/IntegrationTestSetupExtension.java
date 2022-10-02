package io.mongock.driver.mongodb.test.template.extension;

import io.mongock.driver.mongodb.test.template.shared.IntegrationTestShared;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;

public class IntegrationTestSetupExtension implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback {

  private static final String MONGO_CONTAINER = "mongo:4.4.0";
  private static final Integer MONGO_PORT = 27017;
  protected static final String DEFAULT_DATABASE_NAME = "test_container";
  protected static final String CHANGELOG_COLLECTION_NAME = "mongockChangeLog";
  protected static final String LOCK_COLLECTION_NAME = "mongockLock";
  
  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    IntegrationTestShared.setMongoContainer(new GenericContainer(MONGO_CONTAINER).withExposedPorts(MONGO_PORT));
    IntegrationTestShared.getMongoContainer().start();
  }
  
  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    MongoClientSettings settings = MongoClientSettings.builder()
        .writeConcern(getDefaultConnectionWriteConcern())
        .applyConnectionString(new ConnectionString(String.format("mongodb://%s:%d", 
                                                    IntegrationTestShared.getMongoContainer().getContainerIpAddress(), 
                                                    IntegrationTestShared.getMongoContainer().getFirstMappedPort())))
        .build();
    IntegrationTestShared.setMongoClient(MongoClients.create(settings));
    IntegrationTestShared.setMongoDataBase(IntegrationTestShared.getMongoClient().getDatabase(DEFAULT_DATABASE_NAME));
  }
  
  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    IntegrationTestShared.getMongoDataBase().getCollection(CHANGELOG_COLLECTION_NAME).deleteMany(new Document());
    IntegrationTestShared.getMongoDataBase().getCollection(LOCK_COLLECTION_NAME).deleteMany(new Document());
    IntegrationTestShared.getMongoDataBase().drop();
  }

  //Default write concern for the connection.
  //If the Mongock doesn't set the acknowledgement at operation level(in collection),
  // lockRepository will throw UnsupportedOperationException at starting time
  protected WriteConcern getDefaultConnectionWriteConcern() {
    return WriteConcern.UNACKNOWLEDGED;
  }
}
