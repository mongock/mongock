package com.github.cloudyrock.mongock.test.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.DB;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;


@ChangeLog(order = "1")
public class MongockTestResource {

  @ChangeSet(id = "test-replace", order = "001", author = "Mongock", runAlways = true)
  public void testReplace(MongoDatabase db) {
    System.out.println("test-replace");
    MongoCollection<Document> testCollection = db.getCollection("TestCollection");
    Document testDocument = new Document("_id", "testId").append("testField", "testValue");
    testCollection.insertOne(testDocument);

    FindIterable iter = testCollection.find(new Document("_id", "testId"));
    System.out.println("Yeah");
    System.out.println(iter.first());
  }

}
