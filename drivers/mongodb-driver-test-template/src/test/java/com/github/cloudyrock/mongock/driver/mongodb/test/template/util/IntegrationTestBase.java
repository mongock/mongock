package com.github.cloudyrock.mongock.driver.mongodb.test.template.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.testcontainers.containers.GenericContainer;

public abstract class IntegrationTestBase {


  private static final String MONGO_CONTAINER = "mongo:3.1.5";
  private static final Integer MONGO_PORT = 27017;
  private static final String DEFAULT_DATABASE_NAME = "test_container";
  protected static final String CHANGELOG_COLLECTION_NAME = "changockChangeLog";
  protected static final String LOCK_COLLECTION_NAME = "changockLock";
  private MongoDatabase mongoDatabase;
  private MongoClient mongoClient;

  @ClassRule
  public static GenericContainer mongo = new GenericContainer(MONGO_CONTAINER).withExposedPorts(MONGO_PORT);

  @Before
  public final void setUpParent() {
    mongoClient = MongoClients.create(String.format("mongodb://%s:%d", mongo.getContainerIpAddress(), mongo.getFirstMappedPort()));
    mongoDatabase = mongoClient.getDatabase(DEFAULT_DATABASE_NAME);

  }

  @After
  public void tearDown() {
    getDataBase().getCollection(CHANGELOG_COLLECTION_NAME).deleteMany(new Document());
    getDataBase().getCollection(LOCK_COLLECTION_NAME).deleteMany(new Document());
    mongoDatabase.drop();
  }

  protected MongoDatabase getDataBase() {
    return mongoDatabase;
  }

  protected MongoClient getMongoClient() {
    return mongoClient;
  }

  protected MongoDBDriverTestAdapter getDefaultAdapter() {
    return getAdapter(CHANGELOG_COLLECTION_NAME);
  }

  protected abstract MongoDBDriverTestAdapter getAdapter(String collectionName);
}
