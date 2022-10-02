package io.mongock.driver.mongodb.test.template.shared;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.testcontainers.containers.GenericContainer;

public class IntegrationTestShared {
  
  private static GenericContainer mongoContainer;
  private static MongoClient mongoClient;
  private static MongoDatabase mongoDatabase;
  
  public static GenericContainer getMongoContainer() {
    return mongoContainer;
  }
  
  public static void setMongoContainer(GenericContainer mongoContainer) {
    IntegrationTestShared.mongoContainer = mongoContainer;
  }
  
  public static MongoClient getMongoClient() {
    return mongoClient;
  }
  
  public static void setMongoClient(MongoClient mongoClient) {
    IntegrationTestShared.mongoClient = mongoClient;
  }
  
  public static MongoDatabase getMongoDataBase() {
    return mongoDatabase;
  }
  
  public static void setMongoDataBase(MongoDatabase mongoDatabase) {
    IntegrationTestShared.mongoDatabase = mongoDatabase;
  }
}
