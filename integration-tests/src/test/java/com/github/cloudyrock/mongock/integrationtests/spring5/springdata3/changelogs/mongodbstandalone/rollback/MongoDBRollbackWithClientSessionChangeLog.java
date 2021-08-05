package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.mongodbstandalone.rollback;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.Mongock4Spring5SpringData3App;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.client.Client;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ChangeLog(order = "2")
public class MongoDBRollbackWithClientSessionChangeLog {

  @ChangeSet(id = "method-successful", order = "001", author = "mongock")
  public void methodSuccessful(ClientSession session, MongoDatabase db) {

    CodecRegistry pojoCodecRegistry = org.bson.codecs.configuration.CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), org.bson.codecs.configuration.CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    MongoCollection<Client> clientCollection = db.withCodecRegistry(pojoCodecRegistry).getCollection(Mongock4Spring5SpringData3App.CLIENTS_COLLECTION_NAME, Client.class);

    List<Client> clients = IntStream.range(0, 10)
        .mapToObj(i -> new Client("name-" + i, "email-" + i, "phone" + i, "country" + i))
        .collect(Collectors.toList());
    clientCollection.insertMany(session, clients);
  }

  @ChangeSet(id = "method-failing", order = "002", author = "mongock")
  public void methodFailing() {
    if (true) {
      throw new RuntimeException("Transaction error");
    }
  }


}
