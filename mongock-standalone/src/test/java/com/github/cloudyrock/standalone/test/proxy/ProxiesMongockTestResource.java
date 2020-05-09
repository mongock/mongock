package com.github.cloudyrock.standalone.test.proxy;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;

/**
 *
 * @since 04/04/2018
 */
@ChangeLog(order = "5")
public class ProxiesMongockTestResource {

  @ChangeSet(author = "testuser", id = "ProxyMongoDatabaseTest", order = "02")
  public void testMongoDatabase(MongoDatabase mongoDatabase) {
    MongoCollection collection = mongoDatabase.getCollection("anyCollection");
    collection.find(new BsonDocument());
    collection.find(new BsonDocument());
    System.out.println("invoked ProxyMongoDatabaseTest with db=" + mongoDatabase.toString());
  }

}
