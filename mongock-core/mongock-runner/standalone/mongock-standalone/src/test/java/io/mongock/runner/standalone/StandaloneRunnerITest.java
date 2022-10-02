package io.mongock.runner.standalone;


import com.github.silaev.mongodb.replicaset.MongoDbReplicaSet;
import com.google.common.collect.Streams;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.mongock.api.config.MongockConfiguration;
import io.mongock.api.config.TransactionStrategy;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.entry.ChangeState;
import io.mongock.driver.api.entry.ChangeType;
import io.mongock.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import io.mongock.runner.core.executor.MongockRunner;
import io.mongock.runner.core.executor.system.changes.SystemChangeUnit10001;
import io.mongock.runner.standalone.migration.LongIOChangeUnit;
import io.mongock.runner.standalone.migration.MongoDBAdvanceChangeLog;
import io.mongock.runner.standalone.migration.MongoDBAdvanceChangeLogWithBeforeFailing;
import io.mongock.runner.standalone.migration.MongoDBAdvanceChangeLogWithChangeSetFailing;
import io.mongock.runner.standalone.migration.MongoDBRollbackWithNoClientSessionChangeLog;
import io.mongock.runner.standalone.migration.ServiceStub;
import io.mongock.runner.standalone.util.RunnerTestUtil;
import io.mongock.util.test.Constants;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StandaloneRunnerITest {

  private static MongoClient mongoClient;
  private static MongoDatabase database;
  private static MongoDbReplicaSet mongodbContainer;
  private static RunnerTestUtil runnerTestUtil;

  private static MongoCollection<Document> changeEntryCollection;
  private static MongoCollection<Document> dataCollection1;
  private static MongoCollection<Document> dataCollection2;
  private static MongoCollection<Document> dataCollection3;

  @BeforeAll
  public static void startMongoDBForAllTests() {
    mongodbContainer = MongoDbReplicaSet.builder()
        .mongoDockerImageName("mongo:4.2.6")
//        .replicaSetNumber(2)
        .build();
    mongodbContainer.start();
    mongoClient = MongoClients.create(mongodbContainer.getReplicaSetUrl());
    database = mongoClient.getDatabase(Constants.DEFAULT_DATABASE_NAME);
    runnerTestUtil = new RunnerTestUtil(mongoClient);
  }

  @AfterAll
  public static void closeMongoDBForAllTests() {
    mongoClient.close();
    mongodbContainer.close();
  }

  @AfterEach
  public void cleanCommonDatabaseCollections() {
    drop(changeEntryCollection);
    drop(dataCollection1);
    drop(dataCollection2);
    drop(dataCollection3);
  }

  private static void drop(MongoCollection<Document> collection) {
    if (collection != null) {
      collection.drop();
    }
  }

  @Test
  @DisplayName("SHOULD NOT rollback transaction " +
      "WHEN clientSession is not used " +
      "IF any changeSet throws an exception")
  public void shouldNotRollbackTransaction_WhenClientSessionNotUsed_IfException() {

    // given
    database.createCollection(MongoDBRollbackWithNoClientSessionChangeLog.COLLECTION_NAME);
    MongockException ex = Assertions.assertThrows(MongockException.class,
        () -> runnerTestUtil.getBuilder(MongoDBRollbackWithNoClientSessionChangeLog.class.getName())
            .setTransactionEnabled(true)
            .buildRunner()
            .execute());

    //then
    assertTrue(ex.getMessage().contains("Expected exception in changeLog[Before]"));
    dataCollection1 = database.getCollection(MongoDBRollbackWithNoClientSessionChangeLog.COLLECTION_NAME);
    FindIterable<Document> clients = dataCollection1.find();
    Set<Document> clientsSet = new HashSet<>();
    clients.forEach(clientsSet::add);
    assertEquals(10, clientsSet.size());


    //tear down
    dataCollection1.drop();
  }


  @Test
  @DisplayName("SHOULD automatically rollback changeSet and manually before " +
      "WHEN  second changeLog fails at changeSet " +
      "IF strategy is changeLog and  transactional")
  public void shouldAutomaticallyRollbackAndManuallyBefore_WhenSecondChangeLogFails_IfStrategyChangeUnitAndTransactionalEnabled() {

    // given

    MongockConfiguration config = new MongockConfiguration();
    config.setMigrationRepositoryName(Constants.CHANGELOG_COLLECTION_NAME);
    config.setLockRepositoryName("mongockLock");
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    config.setMigrationScanPackage(Arrays.asList(
        MongoDBAdvanceChangeLog.class.getName()
        , MongoDBAdvanceChangeLogWithChangeSetFailing.class.getName()
    ));

    // checks the four rollbacks were called
    MongoDBAdvanceChangeLog.clear();
    MongoDBAdvanceChangeLogWithChangeSetFailing.clear();
    Assertions.assertThrows(MongockException.class,
        () -> runnerTestUtil.getBuilder()
            .setConfig(config)
            .setTransactionEnabled(true)
            .buildRunner()
            .execute()
    );


    Assertions.assertFalse(MongoDBAdvanceChangeLog.rollbackBeforeCalled, "(1)AdvanceChangeLogWithBefore's Rollback before method wasn't executed");
    Assertions.assertFalse(MongoDBAdvanceChangeLog.rollbackCalled, "(2)AdvanceChangeLogWithBefore's Rollback method wasn't executed");

    assertTrue(MongoDBAdvanceChangeLogWithChangeSetFailing.rollbackBeforeCalled, "(3)AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback before method wasn't executed");
    Assertions.assertFalse(MongoDBAdvanceChangeLogWithChangeSetFailing.rollbackCalled, "(4)AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");

    changeEntryCollection = database.getCollection(config.getMigrationRepositoryName());
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


    dataCollection1 = database.getCollection(MongoDBAdvanceChangeLogWithChangeSetFailing.COLLECTION_NAME);
    FindIterable<Document> clients = dataCollection1.find();
    Set<Document> clientsSet = new HashSet<>();
    clients.forEach(clientsSet::add);
    assertEquals(0, clientsSet.size());

    //CHANGELOG 1
    dataCollection2 = database.getCollection(MongoDBAdvanceChangeLog.COLLECTION_NAME);
    FindIterable<Document> clients1 = dataCollection2.find();
    Set<Document> clientsSet1 = new HashSet<>();
    clients1.forEach(clientsSet1::add);
    assertEquals(11, clientsSet1.size());

    //CHANGELOG2
    dataCollection3 = database.getCollection(MongoDBAdvanceChangeLogWithChangeSetFailing.COLLECTION_NAME);
    FindIterable<Document> clients2 = dataCollection3.find();
    Set<Document> clientsSet2 = new HashSet<>();
    clients2.forEach(clientsSet2::add);
    assertEquals(0, clientsSet2.size());


    //tear down
    dataCollection1.drop();
    dataCollection2.drop();
    dataCollection3.drop();
  }

  @Test
  @DisplayName("SHOULD rollback Manually only changeSets of last changLog and store change entries " +
      "WHEN  second changeLog fails at changeSet " +
      "IF strategy is migration and non transactional")
  public void shouldRollbackManuallyOnlyChangeSetsOfLastChangelogAndStoreChangeEntry_whenSecondChangeLogFailAtChangeSet_ifStrategyIsMigrationAndNonTransactional() throws InterruptedException {

    // given
    MongockConfiguration config = new MongockConfiguration();
    config.setMigrationRepositoryName("mongockChangeLog");
    config.setLockRepositoryName("mongockLock");
    config.setMigrationRepositoryName("mongockChangeLog");
    config.setLockRepositoryName("mongockLock");
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
    Assertions.assertThrows(MongockException.class,
        () -> runnerTestUtil.getBuilder()
            .setConfig(config)
            .setTransactionEnabled(false)
            .buildRunner()
            .execute()
    );


    // checks the four rollbacks were called
    assertTrue(MongoDBAdvanceChangeLog.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS), "AdvanceChangeLogWithBefore's Rollback method wasn't executed");
    assertTrue(MongoDBAdvanceChangeLogWithChangeSetFailing.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS), "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");

    changeEntryCollection = database.getCollection(config.getMigrationRepositoryName());
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
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
    dataCollection1 = database.getCollection(MongoDBAdvanceChangeLog.COLLECTION_NAME);
    FindIterable<Document> clients1 = dataCollection1.find();
    Set<Document> clientsSet1 = new HashSet<>();
    clients1.forEach(clientsSet1::add);
    assertEquals(11, clientsSet1.size());

    //CHANGELOG2
    dataCollection2 = database.getCollection(MongoDBAdvanceChangeLogWithChangeSetFailing.COLLECTION_NAME);
    FindIterable<Document> clients2 = dataCollection2.find();
    Set<Document> clientsSet2 = new HashSet<>();
    clients2.forEach(clientsSet2::add);
    assertEquals(10, clientsSet2.size());

    //tear down
    dataCollection1.drop();
    dataCollection2.drop();
  }

  @Test
  @DisplayName("SHOULD rollback Manually only changeSets of last changLog and store change entries " +
      "WHEN  second changeLog fails at changeSet " +
      "IF strategy is changeLog and non transactional")
  public void shouldRollbackManuallyOnlyChangeSetsOfLastChangelogAndStoreChangeEntry_whenSecondChangeLogFailAtChangeSet_ifStrategyIsChangeLogAndNonTransactional() throws InterruptedException {

    // given
    MongockConfiguration config = new MongockConfiguration();
    config.setMigrationRepositoryName("mongockChangeLog");
    config.setLockRepositoryName("mongockLock");
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    config.setChangeLogsScanPackage(Arrays.asList(
        MongoDBAdvanceChangeLog.class.getName(),
        MongoDBAdvanceChangeLogWithChangeSetFailing.class.getName()
    ));

    // checks the four rollbacks were called
    MongoDBAdvanceChangeLog.clear();
    MongoDBAdvanceChangeLogWithChangeSetFailing.clear();
    Assertions.assertThrows(MongockException.class,
        () -> runnerTestUtil.getBuilder()
            .setConfig(config)
            .setTransactionEnabled(false)
            .buildRunner()
            .execute()
    );


    // checks the four rollbacks were called
    assertTrue(MongoDBAdvanceChangeLogWithChangeSetFailing.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS), "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");

    changeEntryCollection = database.getCollection(config.getMigrationRepositoryName());
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
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
    dataCollection1 = database.getCollection(MongoDBAdvanceChangeLog.COLLECTION_NAME);
    FindIterable<Document> clients1 = dataCollection1.find();
    Set<Document> clientsSet1 = new HashSet<>();
    clients1.forEach(clientsSet1::add);
    assertEquals(11, clientsSet1.size());

    //CHANGELOG2
    dataCollection2 = database.getCollection(MongoDBAdvanceChangeLogWithChangeSetFailing.COLLECTION_NAME);
    FindIterable<Document> clients2 = dataCollection2.find();
    Set<Document> clientsSet2 = new HashSet<>();
    clients2.forEach(clientsSet2::add);
    assertEquals(10, clientsSet2.size());

    //tear down
    dataCollection1.drop();
    dataCollection2.drop();

  }

  @Test
  @DisplayName("SHOULD rollback automatically everything and no changeEntry should be present " +
      "WHEN  second changeLog fails at changeSet " +
      "IF strategy is migration and  transactional")
  public void shouldRollbackAutomaticallyEverythingAndNoChangeEntryShouldBePresent_whenSecondChangeLogFailAtChangeSet_ifStrategyIsMigrationAndTransactional() {

    // given

    MongockConfiguration config = new MongockConfiguration();
    config.setMigrationRepositoryName("mongockChangeLog");
    config.setLockRepositoryName("mongockLock");
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
    Assertions.assertThrows(MongockException.class,
        () -> runnerTestUtil.getBuilder()
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
    changeEntryCollection = database.getCollection(config.getMigrationRepositoryName());
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);
    assertEquals(0, changeEntryList.size());

    //CHANGELOG 1
    dataCollection1 = database.getCollection(MongoDBAdvanceChangeLog.COLLECTION_NAME);
    FindIterable<Document> clients1 = dataCollection1.find();
    Set<Document> clientsSet1 = new HashSet<>();
    clients1.forEach(clientsSet1::add);
    assertEquals(1, clientsSet1.size());

    //CHANGELOG2
    dataCollection2 = database.getCollection(MongoDBAdvanceChangeLogWithChangeSetFailing.COLLECTION_NAME);
    FindIterable<Document> clients2 = dataCollection2.find();
    Set<Document> clientsSet2 = new HashSet<>();
    clients2.forEach(clientsSet2::add);
    assertEquals(0, clientsSet2.size());

    //tear down
    dataCollection1.drop();
    dataCollection2.drop();

  }


  @Test
  @DisplayName("SHOULD not rollback " +
      "WHEN changeSet runs normally " +
      "IF strategy is changeLog and  transactional")
  public void shouldNotRollback_WhenChangeSetRunsNormally_IfStrategyChangeLogAndTransactional() {

    // given
    MongockConfiguration config = new MongockConfiguration();
    config.setMigrationRepositoryName("mongockChangeLog");
    config.setLockRepositoryName("mongockLock");
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    config.setMigrationScanPackage(Collections.singletonList(MongoDBAdvanceChangeLog.class.getName()));

    // checks the four rollbacks were called
    MongoDBAdvanceChangeLog.clear();
    runnerTestUtil.getBuilder()
        .setConfig(config)
        .setTransactionEnabled(true)
        .buildRunner()
        .execute();


    Assertions.assertFalse(MongoDBAdvanceChangeLog.rollbackBeforeCalled, "(1)AdvanceChangeLogWithBefore's Rollback before method wasn't executed");
    Assertions.assertFalse(MongoDBAdvanceChangeLog.rollbackCalled, "(2)AdvanceChangeLogWithBefore's Rollback method wasn't executed");

    changeEntryCollection = database.getCollection(config.getMigrationRepositoryName());
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);
    assertEquals(2, changeEntryList.size());

    assertEquals(MongoDBAdvanceChangeLog.class.getSimpleName() + "_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(0).getString("state"));

    assertEquals(MongoDBAdvanceChangeLog.class.getSimpleName(), changeEntryList.get(1).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(1).getString("state"));

    //CHANGELOG 1
    dataCollection1 = database.getCollection(MongoDBAdvanceChangeLog.COLLECTION_NAME);
    FindIterable<Document> clients1 = dataCollection1.find();
    Set<Document> clientsSet1 = new HashSet<>();
    clients1.forEach(clientsSet1::add);
    assertEquals(11, clientsSet1.size());

    //tear down
    dataCollection1.drop();
  }

  @Test
  @DisplayName("SHOULD not run changeSet " +
      "WHEN before throws exception " +
      "IF strategy is changeLog and  transactional ")
  public void shouldNotRunChangeSet_WhenStrategyIsChangeLogAndTransactional_IfBeforeThrowsException() {

    // given
    MongockConfiguration config = new MongockConfiguration();
    config.setMigrationRepositoryName("mongockChangeLog");
    config.setLockRepositoryName("mongockLock");
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    config.setMigrationScanPackage(Collections.singletonList(MongoDBAdvanceChangeLogWithBeforeFailing.class.getName()));

    // checks the four rollbacks were called
    MongoDBAdvanceChangeLogWithBeforeFailing.clear();
    Assertions.assertThrows(MongockException.class,
        () -> runnerTestUtil.getBuilder()
            .setConfig(config)
            .setTransactionEnabled(true)
            .buildRunner()
            .execute()
    );


    assertTrue(MongoDBAdvanceChangeLogWithBeforeFailing.rollbackBeforeCalled, "AdvanceChangeLogWithBeforeFailing's rollback before is expected to be called");
    Assertions.assertFalse(MongoDBAdvanceChangeLogWithBeforeFailing.rollbackCalled, "AdvanceChangeLogWithBeforeFailing's rollback is not expected to be called");
    Assertions.assertFalse(MongoDBAdvanceChangeLogWithBeforeFailing.changeSetCalled, "AdvanceChangeLogWithBeforeFailing's changeSet is not expected to be called");

    changeEntryCollection = database.getCollection(config.getMigrationRepositoryName());
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);
    assertEquals(1, changeEntryList.size());

    assertEquals(MongoDBAdvanceChangeLogWithBeforeFailing.class.getSimpleName() + "_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(0).getString("state"));

    dataCollection1 = database.getCollection(MongoDBAdvanceChangeLogWithBeforeFailing.COLLECTION_NAME);
    FindIterable<Document> clients = dataCollection1.find();
    Set<Document> clientsSet = new HashSet<>();
    clients.forEach(clientsSet::add);
    assertEquals(0, clientsSet.size());


    //tear down
    dataCollection1.drop();
    changeEntryCollection.drop();
  }

  @Test
  @DisplayName("SHOULD keep the lock" +
      "WHEN i/o operation is longer than the ")
  public void test() {
    MongoSync4Driver driver = MongoSync4Driver.withLockStrategy(
        mongoClient,
        Constants.DEFAULT_DATABASE_NAME,
        3000L,
        1000L,
        1000L
    );


    runnerTestUtil.getBuilder(driver, LongIOChangeUnit.class.getName())
        .setTransactionEnabled(true)
        .addDependency(new ServiceStub())
        .buildRunner()
        .execute();

  }
  
  @Test
  public void shouldExecuteSystemUpdateChanges() {

    // given
    MongockRunner runner = runnerTestUtil.getBuilder(true, "dummy.package")
        .buildRunner();

    // when
    runner.execute();
    
    // then
    List<Document> changeEntries = getCurrentChangeEntries();
    
    // ChangeEntry count
    assertEquals(2, changeEntries.size());
    
    // SystemChangeUnit10001 -> BEFORE_EXECUTION
    Document changeEntryDoc = changeEntries.get(0);
    assertEquals(SystemChangeUnit10001.class.getName(), changeEntryDoc.get("changeLogClass"));
    assertEquals("system-change-10001_before", changeEntryDoc.get("changeId"));
    assertEquals("mongock", changeEntryDoc.get("author"));
    assertEquals(ChangeState.EXECUTED.toString(), changeEntryDoc.get("state"));
    assertEquals(ChangeType.BEFORE_EXECUTION.toString(), changeEntryDoc.get("type"));
    assertEquals(true, changeEntryDoc.get("systemChange"));
    
    // SystemChangeUnit10001 -> EXECUTION
    changeEntryDoc = changeEntries.get(1);
    assertEquals(SystemChangeUnit10001.class.getName(), changeEntryDoc.get("changeLogClass"));
    assertEquals("system-change-10001", changeEntryDoc.get("changeId"));
    assertEquals("mongock", changeEntryDoc.get("author"));
    assertEquals(ChangeState.EXECUTED.toString(), changeEntryDoc.get("state"));
    assertEquals(ChangeType.EXECUTION.toString(), changeEntryDoc.get("type"));
    assertEquals(true, changeEntryDoc.get("systemChange"));
  }
  
  private List<Document> getCurrentChangeEntries() {
      return Streams.stream(database.getCollection(Constants.CHANGELOG_COLLECTION_NAME).find()).collect(Collectors.toList());
  }

}
