package io.mongock.driver.mongodb.reactive.util;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.WriteConcern;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.GenericContainer;

public abstract class IntegrationTestBase {

  protected static final String LOCK_COLLECTION_NAME = "mongockLock";
  private static final String MONGO_CONTAINER = "mongo:4.4.0";
  private static final Integer MONGO_PORT = 27017;
  protected static final String DEFAULT_DATABASE_NAME = "test_container";
  protected static final String CHANGELOG_COLLECTION_NAME = "mongockChangeLog";
  //  protected static final String LOCK_COLLECTION_NAME = "mongockLock";
  private static MongoDatabase mongoDatabase;
  private static MongoClient mongoClient;

  public static GenericContainer mongo;

  @BeforeAll
  public static void createContainerForAll() {
    mongo = new GenericContainer<>(MONGO_CONTAINER).withExposedPorts(MONGO_PORT);
    mongo.start();
    MongoClientSettings settings = MongoClientSettings.builder()
        .writeConcern(getDefaultConnectionWriteConcern())
        .applyConnectionString(new ConnectionString(String.format("mongodb://%s:%d", mongo.getContainerIpAddress(), mongo.getFirstMappedPort())))
        .build();
    mongoClient = MongoClients.create(settings);
    mongoDatabase = mongoClient.getDatabase(DEFAULT_DATABASE_NAME);
  }

//  @AfterAll
//  public void removeContainerForAll() {
//    mongo.close();
//  }

  //
  @BeforeEach
  public final void setUpParent() {
//    MongoClientSettings settings = MongoClientSettings.builder()
//        .writeConcern(getDefaultConnectionWriteConcern())
//        .applyConnectionString(new ConnectionString(String.format("mongodb://%s:%d", mongo.getContainerIpAddress(), mongo.getFirstMappedPort())))
//        .build();
//    mongoClient = MongoClients.create(settings);
    mongoDatabase = mongoClient.getDatabase(DEFAULT_DATABASE_NAME);

  }
  //
  @AfterEach
  public void tearDown() {
    SubscriberSync<DeleteResult> subscriber1 = new SubscriberSync<>();
    SubscriberSync<DeleteResult> subscriber2 = new SubscriberSync<>();
    getDataBase().getCollection(CHANGELOG_COLLECTION_NAME).deleteMany(new Document()).subscribe(subscriber1);
    getDataBase().getCollection(LOCK_COLLECTION_NAME).deleteMany(new Document()).subscribe(subscriber2);
    subscriber1.get();
    subscriber2.get();
    SubscriberSync<Void> subscriber3 = new SubscriberSync<>();
    mongoDatabase.drop().subscribe(subscriber3);
    subscriber3.get();
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


  //Default write concern for the connection.
  //If the Mongock doesn't set the acknowledgement at operation level(in collection),
  // lockRepository will throw UnsupportedOperationException at starting time
  protected static WriteConcern getDefaultConnectionWriteConcern() {
    return WriteConcern.UNACKNOWLEDGED;
  }

  protected abstract MongoDBDriverTestAdapter getAdapter(String collectionName);

}
