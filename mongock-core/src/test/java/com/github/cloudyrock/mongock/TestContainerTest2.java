package com.github.cloudyrock.mongock;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

public class TestContainerTest2 {


  private static final String MONGO_CONTAINER = "mongo:3.1.5";
  private static final Integer MONGO_PORT = 27017;
  private static final int CONFIG_SERVICE_PORT = 8888;

  @Rule
  public  GenericContainer mongo = new GenericContainer(MONGO_CONTAINER).withExposedPorts(MONGO_PORT);
  private MongoClient mongoClient;

  @Before
  public void setUp() {
    mongoClient = new MongoClient(mongo.getContainerIpAddress(), mongo.getFirstMappedPort());
  }


  @Test
  public void containerStartsAndPublicPortIsAvailable() {

    new MongockBuilder(mongoClient,"mongock_integration_test", "com.github.cloudyrock.mongock.test.changelogs")
        .build()
        .execute();

  }



  @Test
  public void containerStartsAndPublicPortIsAvailable2() {

    new MongockBuilder(mongoClient,"mongock_integration_test", "com.github.cloudyrock.mongock.test.changelogs")
        .build()
        .execute();

  }


}
