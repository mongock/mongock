package com.github.cloudyrock.mongock.utils;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
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
    MongoClient mongoClient = new MongoClient(mongo.getContainerIpAddress(), mongo.getFirstMappedPort());
    db = mongoClient.getDatabase(DEFAULT_DATABASE_NAME);
  }
}
