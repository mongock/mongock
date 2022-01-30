package io.mongock.runner.standalone.migration;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.runner.standalone.domain.Client;
import io.mongock.util.test.ExpectedException;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ChangeUnit(id="MongoDBAdvanceChangeLogWithChangeSetFailing", order = "2", author = "mongock_test", systemVersion = "1")
public class MongoDBAdvanceChangeLogWithChangeSetFailing {

  public static final String COLLECTION_NAME = MongoDBAdvanceChangeLogWithChangeSetFailing.class.getSimpleName() + "Collection";

  public static boolean rollbackCalled = false;
  public static boolean rollbackBeforeCalled = false;
  public final static CountDownLatch rollbackCalledLatch = new CountDownLatch(2);
  private final MongoDatabase db;
  private final ClientSession session;

  public static void clear() {
    rollbackCalled = false;
    rollbackBeforeCalled = false;
  }

  public MongoDBAdvanceChangeLogWithChangeSetFailing(ClientSession session, MongoDatabase db) {
    this.session = session;
    this.db = db;
  }

  @Execution
  public void changeSet() {
    CodecRegistry pojoCodecRegistry = org.bson.codecs.configuration.CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), org.bson.codecs.configuration.CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    MongoCollection<Client> clientCollection = db.withCodecRegistry(pojoCodecRegistry).getCollection(MongoDBAdvanceChangeLogWithChangeSetFailing.COLLECTION_NAME, Client.class);

    List<Client> clients = IntStream.range(0, 10)
        .mapToObj(i -> new Client("name-" + i, "email-" + i, "phone" + i, "country" + i))
        .collect(Collectors.toList());
    clientCollection.insertMany(session, clients);
    rollbackCalled = false;
    rollbackBeforeCalled = false;
    if(true) throw new ExpectedException("Expected exception in " + MongoDBAdvanceChangeLogWithChangeSetFailing.class + " changeLog[ChangeSet]");
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
    MongoCollection<Client> clientCollection = db.withCodecRegistry(pojoCodecRegistry).getCollection(MongoDBAdvanceChangeLogWithChangeSetFailing.COLLECTION_NAME, Client.class);
  }

  @RollbackBeforeExecution
  public void rollbackBefore() {
    rollbackBeforeCalled = true;
    rollbackCalledLatch.countDown();
  }

}
