package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.testcontainers.containers.GenericContainer;

public class IntegrationTestBase {


    private static final String MONGO_CONTAINER = "mongo:3.1.5";
    private static final Integer MONGO_PORT = 27017;
    private static final String DEFAULT_DATABASE_NAME = "test";
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    protected MongoCollection<Document> collection;

    @ClassRule
    public static GenericContainer mongo = new GenericContainer(MONGO_CONTAINER).withExposedPorts(MONGO_PORT);

    @Before
    public final void setUpParent() {
        mongoClient = MongoClients.create(String.format("mongodb://%s:%d", mongo.getContainerIpAddress(), mongo.getFirstMappedPort()));
        mongoDatabase = mongoClient.getDatabase(DEFAULT_DATABASE_NAME);
    }

    @After
    public void tearDown() {
        collection.deleteMany(new Document());
    }

    public MongoDatabase getDataBase() {
        return mongoDatabase;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }
}
