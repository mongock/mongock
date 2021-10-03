package io.mongock.integrationtests.spring5.springdata3;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.api.config.TransactionStrategy;
import io.mongock.driver.api.entry.ChangeState;
import io.mongock.api.exception.MongockException;
import io.mongock.integrationtests.spring5.springdata3.changelogs.interfaces.mongodbstandalone.rollback.MongoDBAdvanceChangeLog;
import io.mongock.integrationtests.spring5.springdata3.changelogs.interfaces.mongodbstandalone.rollback.MongoDBAdvanceChangeLogWithBeforeFailing;
import io.mongock.integrationtests.spring5.springdata3.changelogs.interfaces.mongodbstandalone.rollback.MongoDBAdvanceChangeLogWithChangeSetFailing;
import io.mongock.integrationtests.spring5.springdata3.changelogs.interfaces.mongodbstandalone.withoutsession.MongoDBRollbackWithNoClientSessionChangeLog;
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
  void shouldNotRollbackTransaction_WhenClientSessionNotUsed_IfException(String mongoVersion) {
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
        MongoDBAdvanceChangeLog.class.getName()
        , MongoDBAdvanceChangeLogWithChangeSetFailing.class.getName()
    ));

    // checks the four rollbacks were called
    MongoDBAdvanceChangeLog.clear();
    MongoDBAdvanceChangeLogWithChangeSetFailing.clear();
    MongockException ex = Assertions.assertThrows(MongockException.class,
        () -> getStandaloneBuilderWithMongoDBSync4( )
            .setConfig(config)
            .setTransactionEnabled(true)
            .buildRunner()
            .execute()
    );


    Assertions.assertFalse(MongoDBAdvanceChangeLog.rollbackBeforeCalled, "(1)AdvanceChangeLogWithBefore's Rollback before method wasn't executed");
    Assertions.assertFalse(MongoDBAdvanceChangeLog.rollbackCalled, "(2)AdvanceChangeLogWithBefore's Rollback method wasn't executed");

    assertTrue(MongoDBAdvanceChangeLogWithChangeSetFailing.rollbackBeforeCalled, "(3)AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback before method wasn't executed");
    Assertions.assertFalse(MongoDBAdvanceChangeLogWithChangeSetFailing.rollbackCalled, "(4)AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");

    MongoCollection<Document> changeEntryCollection = database.getCollection(config.getChangeLogRepositoryName());
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);
    assertEquals(3, changeEntryList.size());

    assertEquals(MongoDBAdvanceChangeLog.class.getSimpleName() + "_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(0).getString("state"));

    assertEquals(MongoDBAdvanceChangeLog.class.getSimpleName(), changeEntryList.get(1).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(1).getString("state"));

    assertEquals(MongoDBAdvanceChangeLogWithChangeSetFailing.class.getSimpleName() + "_before", changeEntryList.get(2).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(2).getString("state"));


    MongoCollection<Document> dataCollection = database.getCollection(MongoDBAdvanceChangeLogWithChangeSetFailing.COLLECTION_NAME);
    FindIterable<Document> clients = dataCollection.find();
    Set<Document> clientsSet = new HashSet<>();
    clients.forEach(clientsSet::add);
    assertEquals(0, clientsSet.size());

    //CHANGELOG 1
    MongoCollection<Document> dataCollection1 = database.getCollection(MongoDBAdvanceChangeLog.COLLECTION_NAME);
    FindIterable<Document> clients1 = dataCollection1.find();
    Set<Document> clientsSet1 = new HashSet<>();
    clients1.forEach(clientsSet1::add);
    assertEquals(11, clientsSet1.size());

    //CHANGELOG2
    MongoCollection<Document> dataCollection2 = database.getCollection(MongoDBAdvanceChangeLogWithChangeSetFailing.COLLECTION_NAME);
    FindIterable<Document> clients2 = dataCollection2.find();
    Set<Document> clientsSet2 = new HashSet<>();
    clients2.forEach(clientsSet2::add);
    assertEquals(0, clientsSet2.size());
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
        MongoDBAdvanceChangeLog.class.getName(),
        MongoDBAdvanceChangeLogWithChangeSetFailing.class.getName()
    ));
    config.setTransactionStrategy(TransactionStrategy.EXECUTION);

    // checks the four rollbacks were called
    MongoDBAdvanceChangeLog.clear();
    MongoDBAdvanceChangeLogWithChangeSetFailing.clear();
    MongockException ex = Assertions.assertThrows(MongockException.class,
        () -> getStandaloneBuilderWithMongoDBSync4( )
            .setConfig(config)
            .setTransactionEnabled(false)
            .buildRunner()
            .execute()
    );


    // checks the four rollbacks were called
    assertTrue(MongoDBAdvanceChangeLog.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS), "AdvanceChangeLogWithBefore's Rollback method wasn't executed");
    assertTrue(MongoDBAdvanceChangeLogWithChangeSetFailing.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS), "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");

    MongoCollection<Document> clientCollection = database.getCollection(config.getChangeLogRepositoryName());
    FindIterable<Document> changeEntryIterator = clientCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);

    assertEquals(4, changeEntryList.size());

    assertEquals(MongoDBAdvanceChangeLog.class.getSimpleName() + "_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(0).getString("state"));

    assertEquals(MongoDBAdvanceChangeLog.class.getSimpleName(), changeEntryList.get(1).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(1).getString("state"));

    assertEquals(MongoDBAdvanceChangeLogWithChangeSetFailing.class.getSimpleName() + "_before", changeEntryList.get(2).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(2).getString("state"));

    assertEquals(MongoDBAdvanceChangeLogWithChangeSetFailing.class.getSimpleName(), changeEntryList.get(3).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(3).getString("state"));

    //CHANGELOG 1
    MongoCollection<Document> dataCollection1 = database.getCollection(MongoDBAdvanceChangeLog.COLLECTION_NAME);
    FindIterable<Document> clients1 = dataCollection1.find();
    Set<Document> clientsSet1 = new HashSet<>();
    clients1.forEach(clientsSet1::add);
    assertEquals(11, clientsSet1.size());

    //CHANGELOG2
    MongoCollection<Document> dataCollection2 = database.getCollection(MongoDBAdvanceChangeLogWithChangeSetFailing.COLLECTION_NAME);
    FindIterable<Document> clients2 = dataCollection2.find();
    Set<Document> clientsSet2 = new HashSet<>();
    clients2.forEach(clientsSet2::add);
    assertEquals(10, clientsSet2.size());
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
        MongoDBAdvanceChangeLog.class.getName(),
        MongoDBAdvanceChangeLogWithChangeSetFailing.class.getName()
    ));

    // checks the four rollbacks were called
    MongoDBAdvanceChangeLog.clear();
    MongoDBAdvanceChangeLogWithChangeSetFailing.clear();
    MongockException ex = Assertions.assertThrows(MongockException.class,
        () -> getStandaloneBuilderWithMongoDBSync4( )
            .setConfig(config)
            .setTransactionEnabled(false)
            .buildRunner()
            .execute()
    );


    // checks the four rollbacks were called
    assertTrue(MongoDBAdvanceChangeLogWithChangeSetFailing.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS), "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");

    MongoCollection<Document> clientCollection = database.getCollection(config.getChangeLogRepositoryName());
    FindIterable<Document> changeEntryIterator = clientCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);

    assertEquals(4, changeEntryList.size());

    assertEquals(MongoDBAdvanceChangeLog.class.getSimpleName() + "_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(0).getString("state"));

    assertEquals(MongoDBAdvanceChangeLog.class.getSimpleName(), changeEntryList.get(1).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(1).getString("state"));

    assertEquals(MongoDBAdvanceChangeLogWithChangeSetFailing.class.getSimpleName() + "_before", changeEntryList.get(2).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(2).getString("state"));

    assertEquals(MongoDBAdvanceChangeLogWithChangeSetFailing.class.getSimpleName(), changeEntryList.get(3).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(3).getString("state"));

    //CHANGELOG 1
    MongoCollection<Document> dataCollection1 = database.getCollection(MongoDBAdvanceChangeLog.COLLECTION_NAME);
    FindIterable<Document> clients1 = dataCollection1.find();
    Set<Document> clientsSet1 = new HashSet<>();
    clients1.forEach(clientsSet1::add);
    assertEquals(11, clientsSet1.size());

    //CHANGELOG2
    MongoCollection<Document> dataCollection2 = database.getCollection(MongoDBAdvanceChangeLogWithChangeSetFailing.COLLECTION_NAME);
    FindIterable<Document> clients2 = dataCollection2.find();
    Set<Document> clientsSet2 = new HashSet<>();
    clients2.forEach(clientsSet2::add);
    assertEquals(10, clientsSet2.size());

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
        MongoDBAdvanceChangeLog.class.getName(),
        MongoDBAdvanceChangeLogWithChangeSetFailing.class.getName()
    ));
    config.setTransactionStrategy(TransactionStrategy.EXECUTION);

    // checks the four rollbacks were called
    MongoDBAdvanceChangeLog.clear();
    MongoDBAdvanceChangeLogWithChangeSetFailing.clear();
    MongockException ex = Assertions.assertThrows(MongockException.class,
        () -> getStandaloneBuilderWithMongoDBSync4( )
            .setConfig(config)
            .setTransactionEnabled(true)
            .buildRunner()
            .execute()
    );

    Assertions.assertFalse(MongoDBAdvanceChangeLog.rollbackBeforeCalled, "AdvanceChangeLogWithBefore's Rollback before method wasn't executed");
    Assertions.assertFalse(MongoDBAdvanceChangeLog.rollbackCalled, "AdvanceChangeLogWithBefore's Rollback method wasn't executed");

    Assertions.assertFalse(MongoDBAdvanceChangeLogWithChangeSetFailing.rollbackBeforeCalled, "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback before method wasn't executed");
    Assertions.assertFalse(MongoDBAdvanceChangeLogWithChangeSetFailing.rollbackCalled, "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");

    //CHANGE ENTRIES
    MongoCollection<Document> clientCollection = database.getCollection(config.getChangeLogRepositoryName());
    FindIterable<Document> changeEntryIterator = clientCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);
    assertEquals(0, changeEntryList.size());

    //CHANGELOG 1
    MongoCollection<Document> dataCollection1 = database.getCollection(MongoDBAdvanceChangeLog.COLLECTION_NAME);
    FindIterable<Document> clients1 = dataCollection1.find();
    Set<Document> clientsSet1 = new HashSet<>();
    clients1.forEach(clientsSet1::add);
    assertEquals(1, clientsSet1.size());

    //CHANGELOG2
    MongoCollection<Document> dataCollection2 = database.getCollection(MongoDBAdvanceChangeLogWithChangeSetFailing.COLLECTION_NAME);
    FindIterable<Document> clients2 = dataCollection2.find();
    Set<Document> clientsSet2 = new HashSet<>();
    clients2.forEach(clientsSet2::add);
    assertEquals(0, clientsSet2.size());


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
    config.setChangeLogsScanPackage(Collections.singletonList(MongoDBAdvanceChangeLog.class.getName()));

    // checks the four rollbacks were called
    MongoDBAdvanceChangeLog.clear();
    getStandaloneBuilderWithMongoDBSync4( )
        .setConfig(config)
        .setTransactionEnabled(true)
        .buildRunner()
        .execute();


    Assertions.assertFalse(MongoDBAdvanceChangeLog.rollbackBeforeCalled, "(1)AdvanceChangeLogWithBefore's Rollback before method wasn't executed");
    Assertions.assertFalse(MongoDBAdvanceChangeLog.rollbackCalled, "(2)AdvanceChangeLogWithBefore's Rollback method wasn't executed");

    MongoCollection<Document> changeEntryCollection = database.getCollection(config.getChangeLogRepositoryName());
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);
    assertEquals(2, changeEntryList.size());

    assertEquals(MongoDBAdvanceChangeLog.class.getSimpleName() + "_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(0).getString("state"));

    assertEquals(MongoDBAdvanceChangeLog.class.getSimpleName(), changeEntryList.get(1).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(1).getString("state"));

    //CHANGELOG 1
    MongoCollection<Document> dataCollection1 = database.getCollection(MongoDBAdvanceChangeLog.COLLECTION_NAME);
    FindIterable<Document> clients1 = dataCollection1.find();
    Set<Document> clientsSet1 = new HashSet<>();
    clients1.forEach(clientsSet1::add);
    assertEquals(11, clientsSet1.size());

  }

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  @DisplayName("SHOULD not run changeSet " +
      "WHEN before throws exception " +
      "IF strategy is changeLog and  transactional ")
  public void shouldNotRunChangeSet_WhenStrategyIsChangeLogAndTransactional_IfBeforeThrowsException(String mongoVersion) {
    start(mongoVersion);

    // given
    MongoDatabase database = mongoClient.getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    config.setChangeLogsScanPackage(Collections.singletonList(MongoDBAdvanceChangeLogWithBeforeFailing.class.getName()));

    // checks the four rollbacks were called
    MongoDBAdvanceChangeLogWithBeforeFailing.clear();
    MongockException ex = Assertions.assertThrows(MongockException.class,
        () -> getStandaloneBuilderWithMongoDBSync4( )
            .setConfig(config)
            .setTransactionEnabled(true)
            .buildRunner()
            .execute()
    );



    assertTrue(MongoDBAdvanceChangeLogWithBeforeFailing.rollbackBeforeCalled, "AdvanceChangeLogWithBeforeFailing's rollback before is expected to be called");
    Assertions.assertFalse(MongoDBAdvanceChangeLogWithBeforeFailing.rollbackCalled, "AdvanceChangeLogWithBeforeFailing's rollback is not expected to be called");
    Assertions.assertFalse(MongoDBAdvanceChangeLogWithBeforeFailing.changeSetCalled, "AdvanceChangeLogWithBeforeFailing's changeSet is not expected to be called");

    MongoCollection<Document> changeEntryCollection = database.getCollection(config.getChangeLogRepositoryName());
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);
    assertEquals(1, changeEntryList.size());

    assertEquals(MongoDBAdvanceChangeLogWithBeforeFailing.class.getSimpleName() + "_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(0).getString("state"));

    MongoCollection<Document> dataCollection = database.getCollection(MongoDBAdvanceChangeLog.COLLECTION_NAME);
    FindIterable<Document> clients = dataCollection.find();
    Set<Document> clientsSet = new HashSet<>();
    clients.forEach(clientsSet::add);
    assertEquals(0, clientsSet.size());
  }

}
