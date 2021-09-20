package io.mongock.integrationtests.spring5.springdata3.changelogs.interfaces.mongodbstandalone.rollback;

import io.mongock.api.ChangeLogInfo;
import io.mongock.integrationtests.spring5.springdata3.client.Client;
import io.mongock.api.ChangeLog;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ChangeLogInfo(id="MongoDBAdvanceChangeLogWithBeforeFailing", order = "2", author = "mongock_test", systemVersion = "1")
public class MongoDBAdvanceChangeLogWithBeforeFailing implements ChangeLog {

  public static final String COLLECTION_NAME = MongoDBAdvanceChangeLogWithBeforeFailing.class.getSimpleName() + "Collection";

  public static boolean changeSetCalled = false;
  public static boolean rollbackCalled = false;
  public static boolean rollbackBeforeCalled = false;
  public final static CountDownLatch rollbackCalledLatch = new CountDownLatch(2);
  private final MongoDatabase db;
  private final ClientSession session;

  public static void clear() {
    changeSetCalled = false;
    rollbackCalled = false;
    rollbackBeforeCalled = false;
  }

  public MongoDBAdvanceChangeLogWithBeforeFailing(ClientSession session, MongoDatabase db) {
    this.session = session;
    this.db = db;
  }

  @Override
  public void changeSet() {
    changeSetCalled = true;
    rollbackCalled = false;
    rollbackBeforeCalled = false;

    CodecRegistry pojoCodecRegistry = org.bson.codecs.configuration.CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), org.bson.codecs.configuration.CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    MongoCollection<Client> clientCollection = db.withCodecRegistry(pojoCodecRegistry).getCollection(MongoDBAdvanceChangeLogWithBeforeFailing.COLLECTION_NAME, Client.class);

    List<Client> clients = IntStream.range(0, 10)
        .mapToObj(i -> new Client("name-" + i, "email-" + i, "phone" + i, "country" + i))
        .collect(Collectors.toList());
    clientCollection.insertMany(session, clients);
  }

  @Override
  public void rollback() {
    rollbackCalled = true;
    rollbackCalledLatch.countDown();
  }


  @Override
  public void before() {
    throw new RuntimeException("Expected exception in " + MongoDBAdvanceChangeLogWithBeforeFailing.class + " changeLog[Before]");
  }

  @Override
  public void rollbackBefore() {
    rollbackBeforeCalled = true;
    rollbackCalledLatch.countDown();
  }

}
