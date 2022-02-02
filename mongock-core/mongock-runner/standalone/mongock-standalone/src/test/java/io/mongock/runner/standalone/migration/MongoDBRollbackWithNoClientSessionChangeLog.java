package io.mongock.runner.standalone.migration;

import com.mongodb.MongoClientSettings;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ChangeUnit(id="MongoDBRollbackWithNoClientSessionChangeLog", order = "1", author = "mongock_test", systemVersion = "1")
public class MongoDBRollbackWithNoClientSessionChangeLog {
  public static final String COLLECTION_NAME = MongoDBRollbackWithNoClientSessionChangeLog.class.getSimpleName() + "Collection";
  private final MongoDatabase db;

  public MongoDBRollbackWithNoClientSessionChangeLog(MongoDatabase db) {
    this.db = db;
  }

  @Execution
  public void changeSet() {

    CodecRegistry pojoCodecRegistry = org.bson.codecs.configuration.CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), org.bson.codecs.configuration.CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    MongoCollection<Client> clientCollection = db.withCodecRegistry(pojoCodecRegistry).getCollection(MongoDBRollbackWithNoClientSessionChangeLog.COLLECTION_NAME, Client.class);
    List<Client> clients = IntStream.range(0, 10)
        .mapToObj(i -> new Client("name-" + i, "email-" + i, "phone" + i, "country" + i))
        .collect(Collectors.toList());
    clientCollection.insertMany(clients);
    throw new ExpectedException("Expected exception in changeLog[Before]");
  }

  @BeforeExecution
  public void before() {
  }

  @RollbackBeforeExecution
  public void rollbackBefore() {

  }

  @RollbackExecution
  public void rollback() {

  }
}
