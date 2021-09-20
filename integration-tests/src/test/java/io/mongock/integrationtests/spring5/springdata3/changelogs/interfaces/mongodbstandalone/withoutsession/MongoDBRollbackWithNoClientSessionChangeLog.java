package io.mongock.integrationtests.spring5.springdata3.changelogs.interfaces.mongodbstandalone.withoutsession;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.mongock.api.ChangeLog;
import io.mongock.api.ChangeLogInfo;
import io.mongock.integrationtests.spring5.springdata3.client.Client;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ChangeLogInfo(id="MongoDBRollbackWithNoClientSessionChangeLog", order = "1", author = "mongock_test", systemVersion = "1")
public class MongoDBRollbackWithNoClientSessionChangeLog implements ChangeLog {
  public static final String COLLECTION_NAME = MongoDBRollbackWithNoClientSessionChangeLog.class.getSimpleName() + "Collection";
  private final MongoDatabase db;

  public MongoDBRollbackWithNoClientSessionChangeLog(MongoDatabase db) {
    this.db = db;
  }

  @Override
  public void changeSet() {

    CodecRegistry pojoCodecRegistry = org.bson.codecs.configuration.CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), org.bson.codecs.configuration.CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    MongoCollection<Client> clientCollection = db.withCodecRegistry(pojoCodecRegistry).getCollection(MongoDBRollbackWithNoClientSessionChangeLog.COLLECTION_NAME, Client.class);
    List<Client> clients = IntStream.range(0, 10)
        .mapToObj(i -> new Client("name-" + i, "email-" + i, "phone" + i, "country" + i))
        .collect(Collectors.toList());
    clientCollection.insertMany(clients);
    throw new RuntimeException("Expected exception in changeLog[Before]");
  }

  @Override
  public void before() {
  }

  @Override
  public void rollbackBefore() {

  }

  @Override
  public void rollback() {

  }
}
