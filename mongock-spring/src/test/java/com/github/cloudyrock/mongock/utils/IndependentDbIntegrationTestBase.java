package com.github.cloudyrock.mongock.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.junit.Before;
import org.junit.Rule;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.GenericContainer;

public abstract class IndependentDbIntegrationTestBase {

  protected static final String MONGO_CONTAINER = "mongo:3.1.5";
  protected static final Integer MONGO_PORT = 27017;
  protected static final String DEFAULT_DATABASE_NAME = "mongocktest";

  private MongoClient mongoClient;
  protected MongoTemplate mongoTemplate;

  @Rule
  public GenericContainer mongo = new GenericContainer(MONGO_CONTAINER).withExposedPorts(MONGO_PORT);

  @Before
  public final void setUpParent() {
    String connectionString = String.format("mongodb://%s:%d", mongo.getContainerIpAddress(), mongo.getFirstMappedPort());
    mongoClient = MongoClients.create(connectionString);
    mongoTemplate = new MongoTemplate(mongoClient, DEFAULT_DATABASE_NAME);

  }
}
