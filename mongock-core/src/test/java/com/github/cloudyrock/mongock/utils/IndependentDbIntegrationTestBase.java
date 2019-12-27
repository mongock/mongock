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
  protected MongoDatabase db;

  @Rule
  public GenericContainer mongo = new GenericContainer(MONGO_CONTAINER).withExposedPorts(MONGO_PORT);

  @Before
  public final void setUpParent() {
    MongoClient mongoClient = new MongoClient(mongo.getContainerIpAddress(), mongo.getFirstMappedPort());
    db = mongoClient.getDatabase(DEFAULT_DATABASE_NAME);
  }
}
