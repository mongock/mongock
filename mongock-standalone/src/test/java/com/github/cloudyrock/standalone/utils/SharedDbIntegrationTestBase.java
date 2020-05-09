package com.github.cloudyrock.standalone.utils;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.testcontainers.containers.GenericContainer;

public abstract class SharedDbIntegrationTestBase {

  protected static final String MONGO_CONTAINER = "mongo:3.1.5";
  protected static final Integer MONGO_PORT = 27017;
  protected static final String DEFAULT_DATABASE_NAME = "mongocktest";
  protected static MongoDatabase db;

  @ClassRule
  public static GenericContainer mongo = new GenericContainer(MONGO_CONTAINER).withExposedPorts(MONGO_PORT);

  @BeforeClass
  public static void setUp() {
    System.out.println("Before class");
    String connectionString = String.format("mongodb://%s:%d", mongo.getContainerIpAddress(), mongo.getFirstMappedPort());
    db = MongoClients.create(connectionString).getDatabase(DEFAULT_DATABASE_NAME);

  }
}
