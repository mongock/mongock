package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.interfaces.springdata.rollback;

import com.github.cloudyrock.mongock.interfaces.ChangeLog;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class SpringDataAdvanceChangeLog implements ChangeLog {
  public static final String COLLECTION_NAME = SpringDataAdvanceChangeLog.class.getSimpleName() + "Collection";


  public static boolean rollbackCalled = false;
  public static boolean rollbackBeforeCalled = false;
  public final static CountDownLatch rollbackCalledLatch = new CountDownLatch(2);

  private final MongoTemplate template;
  private MongoCollection<Document> clientCollection;

  public static void clear() {
    rollbackCalled = false;
    rollbackBeforeCalled = false;
  }

  public SpringDataAdvanceChangeLog(MongoTemplate template) {
    this.template = template;
  }
  @Override
  public String geId() {
    return getClass().getSimpleName();
  }
  @Override
  public String getAuthor() {
    return "mongock_test";
  }

  @Override
  public String getOrder() {
    return "1";
  }

  @Override
  public boolean isFailFast() {
    return true;
  }

  @Override
  public String getSystemVersion() {
    return "1";
  }

  @Override
  public void changeSet() {
    rollbackCalled = false;
    rollbackBeforeCalled = false;

    List<Document> clients = IntStream.range(0, 10)
        .mapToObj(i -> new Document().append("name","name-" + i).append("email", "email-" + i).append("phone", "phone" + i).append("country", "country" + i))
        .collect(Collectors.toList());
    clientCollection.insertMany(clients);
  }

  @Override
  public void rollback() {
    rollbackCalled = true;
    rollbackCalledLatch.countDown();
  }

  @Override
  public void before() {
    //creates the collection
    clientCollection = template.createCollection(SpringDataAdvanceChangeLog.COLLECTION_NAME);
    //this is required, otherwise collection doesn't get created and throws an exception in the changeSet
    clientCollection.insertOne(new Document().append("name","name-DUMMY").append("email", "email--DUMMY").append("phone", "phone-DUMMY").append("country", "country-DUMMY"));
  }

  @Override
  public void rollbackBefore() {
    rollbackBeforeCalled = true;
    rollbackCalledLatch.countDown();

  }

}
