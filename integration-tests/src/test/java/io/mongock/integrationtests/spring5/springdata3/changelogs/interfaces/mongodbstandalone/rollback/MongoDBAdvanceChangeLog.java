package io.mongock.integrationtests.spring5.springdata3.changelogs.interfaces.mongodbstandalone.rollback;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.integrationtests.spring5.springdata3.client.Client;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ChangeUnit(id="MongoDBAdvanceChangeLog", order = "1", author = "mongock_test", systemVersion = "1")
public class MongoDBAdvanceChangeLog {
  public static final String COLLECTION_NAME = MongoDBAdvanceChangeLog.class.getSimpleName() + "Collection";


  public static boolean rollbackCalled = false;
  public static boolean rollbackBeforeCalled = false;
  public final static CountDownLatch rollbackCalledLatch = new CountDownLatch(2);

  private final MongoDatabase db;
  private final ClientSession session;
  private MongoCollection<Client> clientCollection;

  public static void clear() {
    rollbackCalled = false;
    rollbackBeforeCalled = false;
  }

  public MongoDBAdvanceChangeLog(ClientSession session, MongoDatabase db) {
    this.session = session;
    this.db = db;
  }

  @Execution
  public void changeSet() {
    rollbackCalled = false;
    rollbackBeforeCalled = false;

    List<Client> clients = IntStream.range(0, 10)
        .mapToObj(i -> new Client("name-" + i, "email-" + i, "phone" + i, "country" + i))
        .collect(Collectors.toList());
    clientCollection.insertMany(session, clients);
  }

  @RollbackExecution
  public void rollback() {
    rollbackCalled = true;
    rollbackCalledLatch.countDown();
  }

  @BeforeExecution
  public void before() {
    //creates the collection
    CodecRegistry pojoCodecRegistry = org.bson.codecs.configuration.CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), org.bson.codecs.configuration.CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    clientCollection = db.withCodecRegistry(pojoCodecRegistry).getCollection(MongoDBAdvanceChangeLog.COLLECTION_NAME, Client.class);
    //this is required, otherwise collection doesn't get created and throws an exception in the changeSet
    clientCollection.insertOne(new Client("name-DUMMY", "email-DUMMY", "phone-DUMMY", "country-DUMMY"));
  }

  @RollbackBeforeExecution
  public void rollbackBefore() {
    rollbackBeforeCalled = true;
    rollbackCalledLatch.countDown();

  }

}
