package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.config.TransactionStrategy;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeState;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.mongodbstandalone.rollback.AdvanceChangeLog;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.mongodbstandalone.rollback.AdvanceChangeLogWithBeforeFailing;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.mongodbstandalone.rollback.AdvanceChangeLogWithChangeSetFailing;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class MongoDBWithRunnerITest extends ApplicationRunnerTestBase {

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  @DisplayName("SHOULD NOT rollback transaction " +
      "WHEN clientSession is not used " +
      "IF any changeSet throws an exception")
  void shouldNotRollbackTransactionWhenClientSessionNotUsedIfException(String mongoVersion) {
    start(mongoVersion);
    // given
    MongoDatabase database = mongoClient.getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);
    database.createCollection(MongoDBRollbackWithNoClientSessionChangeLog.COLLECTION_NAME);
    MongockException ex = Assertions.assertThrows(MongockException.class,
        () -> getStandaloneBuilderWithMongoDBSync4(MongoDBRollbackWithNoClientSessionChangeLog.class.getName())
            .setTransactionEnabled(true)
            .buildRunner()
            .execute());

    //then
    assertTrue( ex.getMessage().contains("Expected exception in changeLog[Before]"));
    MongoCollection<Document> clientCollection = database.getCollection(MongoDBRollbackWithNoClientSessionChangeLog.COLLECTION_NAME);
    FindIterable<Document> clients = clientCollection.find();
    Set<Document> clientsSet = new HashSet<>();
    clients.forEach(clientsSet::add);
    assertEquals(10, clientsSet.size());
  }

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  @DisplayName("SHOULD automatically rollback changeSet and manually before " +
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
        AdvanceChangeLog.class.getName()
        , AdvanceChangeLogWithChangeSetFailing.class.getName()
    ));

    // checks the four rollbacks were called
    AdvanceChangeLog.clear();
    AdvanceChangeLogWithChangeSetFailing.clear();
    MongockException ex = Assertions.assertThrows(MongockException.class,
        () -> getStandaloneBuilderWithMongoDBSync4( )
            .setConfig(config)
            .setTransactionEnabled(true)
            .buildRunner()
            .execute()
    );


    Assertions.assertFalse(AdvanceChangeLog.rollbackBeforeCalled, "(1)AdvanceChangeLogWithBefore's Rollback before method wasn't executed");
    Assertions.assertFalse(AdvanceChangeLog.rollbackCalled, "(2)AdvanceChangeLogWithBefore's Rollback method wasn't executed");

    Assertions.assertTrue(AdvanceChangeLogWithChangeSetFailing.rollbackBeforeCalled, "(3)AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback before method wasn't executed");
    Assertions.assertFalse(AdvanceChangeLogWithChangeSetFailing.rollbackCalled, "(4)AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");

    MongoCollection<Document> changeEntryCollection = database.getCollection(config.getChangeLogRepositoryName());
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);
    assertEquals(4, changeEntryList.size());

    assertEquals(AdvanceChangeLog.class.getSimpleName() + "_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(0).getString("state"));

    assertEquals(AdvanceChangeLog.class.getSimpleName(), changeEntryList.get(1).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(1).getString("state"));

    assertEquals(AdvanceChangeLogWithChangeSetFailing.class.getSimpleName() + "_before", changeEntryList.get(2).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(2).getString("state"));

    assertEquals(AdvanceChangeLogWithChangeSetFailing.class.getSimpleName(), changeEntryList.get(3).getString("changeId"));
    assertEquals(ChangeState.FAILED.name(), changeEntryList.get(3).getString("state"));

    MongoCollection<Document> dataCollection = database.getCollection(AdvanceChangeLogWithChangeSetFailing.COLLECTION_NAME);
    FindIterable<Document> clients = dataCollection.find();
    Set<Document> clientsSet = new HashSet<>();
    clients.forEach(clientsSet::add);
    assertEquals(0, clientsSet.size());
  }

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  @DisplayName("SHOULD rollback Manually only changeSets of last changLog and store change entries " +
      "WHEN  second changeLog fails at changeSet " +
      "IF strategy is migration and non transactional")
  public void shouldRollbackManuallyOnlyChangeSetsOfLastChangelogAndStoreChangeEntry_whenSecondChangeLogFailAtChangeSet_ifStrategyIsMigrationAndNonTransactional(String mongoVersion) throws InterruptedException {
    start(mongoVersion);

    // given

    MongoDatabase database = mongoClient.getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    config.setChangeLogsScanPackage(Arrays.asList(
        AdvanceChangeLog.class.getName(),
        AdvanceChangeLogWithChangeSetFailing.class.getName()
    ));
    config.setTransactionStrategy(TransactionStrategy.MIGRATION);

    // checks the four rollbacks were called
    AdvanceChangeLog.clear();
    AdvanceChangeLogWithChangeSetFailing.clear();
    MongockException ex = Assertions.assertThrows(MongockException.class,
        () -> getStandaloneBuilderWithMongoDBSync4( )
            .setConfig(config)
            .setTransactionEnabled(false)
            .buildRunner()
            .execute()
    );


    // checks the four rollbacks were called
    assertTrue(AdvanceChangeLog.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS), "AdvanceChangeLogWithBefore's Rollback method wasn't executed");
    assertTrue(AdvanceChangeLogWithChangeSetFailing.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS), "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");

    MongoCollection<Document> clientCollection = database.getCollection(config.getChangeLogRepositoryName());
    FindIterable<Document> changeEntryIterator = clientCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);

    assertEquals(4, changeEntryList.size());

    assertEquals(AdvanceChangeLog.class.getSimpleName() + "_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(0).getString("state"));

    assertEquals(AdvanceChangeLog.class.getSimpleName(), changeEntryList.get(1).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(1).getString("state"));

    assertEquals(AdvanceChangeLogWithChangeSetFailing.class.getSimpleName() + "_before", changeEntryList.get(2).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(2).getString("state"));

    assertEquals(AdvanceChangeLogWithChangeSetFailing.class.getSimpleName(), changeEntryList.get(3).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(3).getString("state"));

  }

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  @DisplayName("SHOULD rollback Manually only changeSets of last changLog and store change entries " +
          "WHEN  second changeLog fails at changeSet " +
          "IF strategy is changeLog and non transactional")
  public void shouldRollbackManuallyOnlyChangeSetsOfLastChangelogAndStoreChangeEntry_whenSecondChangeLogFailAtChangeSet_ifStrategyIsChangeLogAndNonTransactional(String mongoVersion) throws InterruptedException {
    start(mongoVersion);

    // given

    MongoDatabase database = mongoClient.getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    config.setChangeLogsScanPackage(Arrays.asList(
        AdvanceChangeLog.class.getName(),
        AdvanceChangeLogWithChangeSetFailing.class.getName()
    ));

    // checks the four rollbacks were called
    AdvanceChangeLog.clear();
    AdvanceChangeLogWithChangeSetFailing.clear();
    MongockException ex = Assertions.assertThrows(MongockException.class,
        () -> getStandaloneBuilderWithMongoDBSync4( )
            .setConfig(config)
            .setTransactionEnabled(false)
            .buildRunner()
            .execute()
    );


    // checks the four rollbacks were called
    assertTrue(AdvanceChangeLogWithChangeSetFailing.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS), "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");

    MongoCollection<Document> clientCollection = database.getCollection(config.getChangeLogRepositoryName());
    FindIterable<Document> changeEntryIterator = clientCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);

    assertEquals(4, changeEntryList.size());

    assertEquals(AdvanceChangeLog.class.getSimpleName() + "_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(0).getString("state"));

    assertEquals(AdvanceChangeLog.class.getSimpleName(), changeEntryList.get(1).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(1).getString("state"));

    assertEquals(AdvanceChangeLogWithChangeSetFailing.class.getSimpleName() + "_before", changeEntryList.get(2).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(2).getString("state"));

    assertEquals(AdvanceChangeLogWithChangeSetFailing.class.getSimpleName(), changeEntryList.get(3).getString("changeId"));
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
        AdvanceChangeLog.class.getName(),
        AdvanceChangeLogWithChangeSetFailing.class.getName()
    ));
    config.setTransactionStrategy(TransactionStrategy.MIGRATION);

    // checks the four rollbacks were called
    AdvanceChangeLog.clear();
    AdvanceChangeLogWithChangeSetFailing.clear();
    MongockException ex = Assertions.assertThrows(MongockException.class,
        () -> getStandaloneBuilderWithMongoDBSync4( )
            .setConfig(config)
            .setTransactionEnabled(true)
            .buildRunner()
            .execute()
    );

    Assertions.assertFalse(AdvanceChangeLog.rollbackBeforeCalled, "AdvanceChangeLogWithBefore's Rollback before method wasn't executed");
    Assertions.assertFalse(AdvanceChangeLog.rollbackCalled, "AdvanceChangeLogWithBefore's Rollback method wasn't executed");

    Assertions.assertFalse(AdvanceChangeLogWithChangeSetFailing.rollbackBeforeCalled, "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback before method wasn't executed");
    Assertions.assertFalse(AdvanceChangeLogWithChangeSetFailing.rollbackCalled, "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");

    MongoCollection<Document> clientCollection = database.getCollection(config.getChangeLogRepositoryName());
    FindIterable<Document> changeEntryIterator = clientCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);
    assertEquals(0, changeEntryList.size());
  }

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  @DisplayName("SHOULD not rollback " +
      "WHEN changeSet runs normally " +
      "IF strategy is changeLog and  transactional")
  public void shouldNotRollback_WhenChangeSetRunsNormally_IfStrategyChangeLogAndTransactional(String mongoVersion) {
    start(mongoVersion);

    // given
    MongoDatabase database = mongoClient.getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    config.setChangeLogsScanPackage(Collections.singletonList(AdvanceChangeLog.class.getName()));

    // checks the four rollbacks were called
    AdvanceChangeLog.clear();
    getStandaloneBuilderWithMongoDBSync4( )
        .setConfig(config)
        .setTransactionEnabled(true)
        .buildRunner()
        .execute();


    Assertions.assertFalse(AdvanceChangeLog.rollbackBeforeCalled, "(1)AdvanceChangeLogWithBefore's Rollback before method wasn't executed");
    Assertions.assertFalse(AdvanceChangeLog.rollbackCalled, "(2)AdvanceChangeLogWithBefore's Rollback method wasn't executed");

    MongoCollection<Document> changeEntryCollection = database.getCollection(config.getChangeLogRepositoryName());
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);
    assertEquals(2, changeEntryList.size());

    assertEquals(AdvanceChangeLog.class.getSimpleName() + "_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(0).getString("state"));

    assertEquals(AdvanceChangeLog.class.getSimpleName(), changeEntryList.get(1).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(1).getString("state"));

    MongoCollection<Document> dataCollection = database.getCollection(AdvanceChangeLog.COLLECTION_NAME);
    FindIterable<Document> clients = dataCollection.find();
    Set<Document> clientsSet = new HashSet<>();
    clients.forEach(clientsSet::add);
    assertEquals(11, clientsSet.size());
  }

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  @DisplayName("SHOULD not run changeSet " +
      "WHEN strategy is changeLog and  transactional " +
      "IF before throws exception")
  public void shouldNotRunChangeSet_WhenStrategyIsChangeLogAndTransactional_IfBeforeThrowsException(String mongoVersion) {
    start(mongoVersion);

    // given
    MongoDatabase database = mongoClient.getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    config.setChangeLogsScanPackage(Collections.singletonList(AdvanceChangeLogWithBeforeFailing.class.getName()));

    // checks the four rollbacks were called
    AdvanceChangeLogWithBeforeFailing.clear();
    MongockException ex = Assertions.assertThrows(MongockException.class,
        () -> getStandaloneBuilderWithMongoDBSync4( )
            .setConfig(config)
            .setTransactionEnabled(true)
            .buildRunner()
            .execute()
    );



    Assertions.assertTrue(AdvanceChangeLogWithBeforeFailing.rollbackBeforeCalled, "AdvanceChangeLogWithBeforeFailing's rollback before is expected to be called");
    Assertions.assertFalse(AdvanceChangeLogWithBeforeFailing.rollbackCalled, "AdvanceChangeLogWithBeforeFailing's rollback is not expected to be called");
    Assertions.assertFalse(AdvanceChangeLogWithBeforeFailing.changeSetCalled, "AdvanceChangeLogWithBeforeFailing's changeSet is not expected to be called");
    MongoCollection<Document> changeEntryCollection = database.getCollection(config.getChangeLogRepositoryName());
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);
    assertEquals(1, changeEntryList.size());

    assertEquals(AdvanceChangeLogWithBeforeFailing.class.getSimpleName() + "_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(0).getString("state"));

    MongoCollection<Document> dataCollection = database.getCollection(AdvanceChangeLog.COLLECTION_NAME);
    FindIterable<Document> clients = dataCollection.find();
    Set<Document> clientsSet = new HashSet<>();
    clients.forEach(clientsSet::add);
    assertEquals(0, clientsSet.size());
  }

}
