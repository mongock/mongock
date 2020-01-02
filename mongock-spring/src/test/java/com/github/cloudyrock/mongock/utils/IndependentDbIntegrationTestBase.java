package com.github.cloudyrock.mongock.utils;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.junit.Before;
import org.junit.Rule;
import org.testcontainers.containers.GenericContainer;

public abstract class IndependentDbIntegrationTestBase {

  protected static final String MONGO_CONTAINER = "mongo:3.1.5";
  protected static final Integer MONGO_PORT = 27017;
  protected static final String DEFAULT_DATABASE_NAME = "mongocktest";
  protected MongoDatabase mongoDatabase;
  protected MongoClient mongoClient;

  @Rule
  public GenericContainer mongo = new GenericContainer(MONGO_CONTAINER).withExposedPorts(MONGO_PORT);

  @Before
  public final void setUpParent() {
    mongoClient = new MongoClient(mongo.getContainerIpAddress(), mongo.getFirstMappedPort());
//    String connectionString = String.format("mongodb://%s:%d", mongo.getContainerIpAddress(), mongo.getFirstMappedPort());
//    mongoClient = MongoClients.create(connectionString);
    mongoDatabase = mongoClient.getDatabase(DEFAULT_DATABASE_NAME);
  }
}
