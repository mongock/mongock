package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3;

import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.mongodbstandalone.rollback.MongoDBRollbackWithClientSessionChangeLog;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.mongodbstandalone.withoutsession.MongoDBRollbackWithNoClientSessionChangeLog;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class MongoDBWithRunnerITest extends ApplicationRunnerTestBase {

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  @DisplayName("SHOULD NOT rollback transaction WHEN clientSession is not used IF any changeSet throws an exception")
  void shouldNotRollbackTransactionWhenClientSessionNotUsedIfException(String mongoVersion) throws Exception {
    start(mongoVersion);
    // given
    MongoDatabase database = mongoClient.getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);
    database.createCollection(Mongock4Spring5SpringData3App.CLIENTS_COLLECTION_NAME);
    MongockException ex = Assertions.assertThrows(MongockException.class,
        () -> getStandaloneBuilderWithMongoDBSync4(MongoDBRollbackWithNoClientSessionChangeLog.class.getPackage().getName())
            .buildRunner()
            .execute());

    //then
    assertEquals("com.github.cloudyrock.mongock.exception.MongockException: Error in method[MongoDBRollbackWithNoClientSessionChangeLog.methodFailing] : Transaction error", ex.getMessage());

    MongoCollection<Document> clientCollection = database.getCollection(Mongock4Spring5SpringData3App.CLIENTS_COLLECTION_NAME);
    FindIterable<Document> clients = clientCollection.find();
    Set<Document> clientsSet = new HashSet<>();
    clients.forEach(clientsSet::add);
    assertEquals(10, clientsSet.size());
  }

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  @DisplayName("SHOULD rollback transaction WHEN clientSession is used IF any changeSet throws an exception")
  void shouldRollbackTransactionWhenClientSessionUsedIfException(String mongoVersion) throws Exception {
    start(mongoVersion);
    // given
    MongoDatabase database = mongoClient.getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);
    database.createCollection(Mongock4Spring5SpringData3App.CLIENTS_COLLECTION_NAME);
    MongockException ex = Assertions.assertThrows(MongockException.class,
        () -> getStandaloneBuilderWithMongoDBSync4(MongoDBRollbackWithClientSessionChangeLog.class.getPackage().getName())
            .buildRunner()
            .execute());

    //then
    assertEquals("com.github.cloudyrock.mongock.exception.MongockException: Error in method[MongoDBRollbackWithClientSessionChangeLog.methodFailing] : Transaction error", ex.getMessage());

    MongoCollection<Document> clientCollection = database.getCollection(Mongock4Spring5SpringData3App.CLIENTS_COLLECTION_NAME);
    FindIterable<Document> clients = clientCollection.find();
    Set<Document> clientsSet = new HashSet<>();
    clients.forEach(clientsSet::add);
    assertEquals(0, clientsSet.size());
  }






}
