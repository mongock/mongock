package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.config.TransactionStrategy;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeState;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.mongodbstandalone.rollback.MongoDBRollbackWithClientSessionChangeLog;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.mongodbstandalone.withoutsession.MongoDBRollbackWithNoClientSessionChangeLog;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.withRollback.AdvanceChangeLogWithBefore;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.withRollback.AdvanceChangeLogWithBeforeAndChangeSetFailing;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  @DisplayName("SHOULD rollback Manually only changeSets of last changLog and store change entries" +
      "WHEN  second changeLog fails at changeSet" +
      "IF strategy is migration and non transactional")
  public void shouldRollbackManuallyOnlyChangeSetsOfLastChangelogAndStoreChangeEntry_whenSecondChangeLogFailAtChangeSet_ifStrategyIsMigrationAndNonTransactional(String mongoVersion) throws InterruptedException {
    start(mongoVersion);

    // given

    MongoDatabase database = mongoClient.getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    config.setChangeLogsScanPackage(Arrays.asList(
        AdvanceChangeLogWithBefore.class.getName(),
        AdvanceChangeLogWithBeforeAndChangeSetFailing.class.getName()
    ));
    config.setTransactionStrategy(TransactionStrategy.MIGRATION);

    MongockException ex = Assertions.assertThrows(MongockException.class,
        () -> getStandaloneBuilderWithMongoDBSync4( )
            .setConfig(config)
            .setTransactionEnabled(false)
            .buildRunner()
            .execute()
    );


    // checks the four rollbacks were called
    assertTrue(AdvanceChangeLogWithBefore.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS), "AdvanceChangeLogWithBefore's Rollback method wasn't executed");
    assertTrue(AdvanceChangeLogWithBeforeAndChangeSetFailing.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS), "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");

    MongoCollection<Document> clientCollection = database.getCollection(config.getChangeLogRepositoryName());
    FindIterable<Document> changeEntryIterator = clientCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);

    assertEquals(4, changeEntryList.size());

    assertEquals("AdvanceChangeLogWithBefore_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(0).getString("state"));

    assertEquals("AdvanceChangeLogWithBefore", changeEntryList.get(1).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(1).getString("state"));

    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing_before", changeEntryList.get(2).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(2).getString("state"));

    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing", changeEntryList.get(3).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(3).getString("state"));

  }

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  @DisplayName("SHOULD rollback Manually only changeSets of last changLog and store change entries" +
          "WHEN  second changeLog fails at changeSet" +
          "IF strategy is changeLog and non transactional")
  public void shouldRollbackManuallyOnlyChangeSetsOfLastChangelogAndStoreChangeEntry_whenSecondChangeLogFailAtChangeSet_ifStrategyIsChangeLogAndNonTransactional(String mongoVersion) throws InterruptedException {
    start(mongoVersion);

    // given

    MongoDatabase database = mongoClient.getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    config.setChangeLogsScanPackage(Arrays.asList(
        AdvanceChangeLogWithBefore.class.getName(),
        AdvanceChangeLogWithBeforeAndChangeSetFailing.class.getName()
    ));

    MongockException ex = Assertions.assertThrows(MongockException.class,
        () -> getStandaloneBuilderWithMongoDBSync4( )
            .setConfig(config)
            .setTransactionEnabled(false)
            .buildRunner()
            .execute()
    );


    // checks the four rollbacks were called
    assertTrue(AdvanceChangeLogWithBeforeAndChangeSetFailing.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS), "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");

    MongoCollection<Document> clientCollection = database.getCollection(config.getChangeLogRepositoryName());
    FindIterable<Document> changeEntryIterator = clientCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);

    assertEquals(4, changeEntryList.size());

    assertEquals("AdvanceChangeLogWithBefore_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(0).getString("state"));

    assertEquals("AdvanceChangeLogWithBefore", changeEntryList.get(1).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(1).getString("state"));

    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing_before", changeEntryList.get(2).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(2).getString("state"));

    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing", changeEntryList.get(3).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(3).getString("state"));

  }

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  @DisplayName("SHOULD rollback automatically everything and no changeEntry should be present " +
      "WHEN  second changeLog fails at changeSet " +
      "IF strategy is migration and  transactional")
  public void shouldRollbackAutomaticallyEverythingAndNoChangeEntryShouldBePresent_whenSecondChangeLogFailAtChangeSet_ifStrategyIsMigrationAndTransactional(String mongoVersion) {
    start(mongoVersion);

    // given
    MongoDatabase database = mongoClient.getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    config.setChangeLogsScanPackage(Arrays.asList(
        AdvanceChangeLogWithBefore.class.getName(),
        AdvanceChangeLogWithBeforeAndChangeSetFailing.class.getName()
    ));
    config.setTransactionStrategy(TransactionStrategy.MIGRATION);

    MongockException ex = Assertions.assertThrows(MongockException.class,
        () -> getStandaloneBuilderWithMongoDBSync4( )
            .setConfig(config)
            .setTransactionEnabled(true)
            .buildRunner()
            .execute()
    );


    // checks the four rollbacks were called
    Assertions.assertFalse(AdvanceChangeLogWithBefore.rollbackBeforeCalled, "AdvanceChangeLogWithBefore's Rollback before method wasn't executed");
    Assertions.assertFalse(AdvanceChangeLogWithBefore.rollbackCalled, "AdvanceChangeLogWithBefore's Rollback method wasn't executed");

    Assertions.assertFalse(AdvanceChangeLogWithBeforeAndChangeSetFailing.rollbackBeforeCalled, "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback before method wasn't executed");
    Assertions.assertFalse(AdvanceChangeLogWithBeforeAndChangeSetFailing.rollbackCalled, "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");

    MongoCollection<Document> clientCollection = database.getCollection(config.getChangeLogRepositoryName());
    FindIterable<Document> changeEntryIterator = clientCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);
    assertEquals(0, changeEntryList.size());
  }

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  @DisplayName("SHOULD not rollback first changeLog and rollback automatically changeSet of second changeLog and manually before of second changeLog " +
      "WHEN  second changeLog fails at changeSet " +
      "IF strategy is changeLog and  transactional")
  public void shouldNotRollbackFirstChangeLogAndRollbackAutomaticallyChangeSetOfSecondChangeLogAndManuallyBeforeOfSecondChangeLog_whenSecondChangeLogFailAtChangeSet_ifStrategyIsChangeLogAndTransactional(String mongoVersion) {
    start(mongoVersion);

    // given
    MongoDatabase database = mongoClient.getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    config.setChangeLogsScanPackage(Arrays.asList(
        AdvanceChangeLogWithBefore.class.getName(),
        AdvanceChangeLogWithBeforeAndChangeSetFailing.class.getName()
    ));

    MongockException ex = Assertions.assertThrows(MongockException.class,
        () -> getStandaloneBuilderWithMongoDBSync4( )
            .setConfig(config)
            .setTransactionEnabled(true)
            .buildRunner()
            .execute()
    );


    // checks the four rollbacks were called
    Assertions.assertFalse(AdvanceChangeLogWithBefore.rollbackBeforeCalled, "AdvanceChangeLogWithBefore's Rollback before method wasn't executed");
    Assertions.assertFalse(AdvanceChangeLogWithBefore.rollbackCalled, "AdvanceChangeLogWithBefore's Rollback method wasn't executed");

    Assertions.assertTrue(AdvanceChangeLogWithBeforeAndChangeSetFailing.rollbackBeforeCalled, "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback before method wasn't executed");
    Assertions.assertFalse(AdvanceChangeLogWithBeforeAndChangeSetFailing.rollbackCalled, "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");

    MongoCollection<Document> changeEntryCollection = database.getCollection(config.getChangeLogRepositoryName());
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);
    assertEquals(4, changeEntryList.size());

    assertEquals("AdvanceChangeLogWithBefore_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(0).getString("state"));

    assertEquals("AdvanceChangeLogWithBefore", changeEntryList.get(1).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(1).getString("state"));

    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing_before", changeEntryList.get(2).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(2).getString("state"));

    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing", changeEntryList.get(3).getString("changeId"));
    assertEquals(ChangeState.FAILED.name(), changeEntryList.get(3).getString("state"));

    MongoCollection<Document> dataCollection = database.getCollection(AdvanceChangeLogWithBeforeAndChangeSetFailing.COLLECTION_NAME);
    FindIterable<Document> clients = dataCollection.find();
    Set<Document> clientsSet = new HashSet<>();
    clients.forEach(clientsSet::add);
    assertEquals(0, clientsSet.size());
  }


}
