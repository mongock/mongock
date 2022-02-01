package io.mongock.runner.springboot;

import com.github.silaev.mongodb.replicaset.MongoDbReplicaSet;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.mongock.api.config.TransactionStrategy;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.entry.ChangeState;
import io.mongock.runner.core.executor.MongockRunner;
import io.mongock.runner.springboot.domain.Client;
import io.mongock.runner.springboot.migration.FailingChangeLogLegacy;
import io.mongock.runner.springboot.migration.SpringDataAdvanceChangeLog;
import io.mongock.runner.springboot.migration.SpringDataAdvanceChangeLogWithBeforeFailing;
import io.mongock.runner.springboot.migration.SpringDataAdvanceChangeLogWithChangeSetFailing;
import io.mongock.runner.springboot.migration.TransactionSuccessfulChangeLog;
import io.mongock.runner.springboot.util.RunnerTestUtil;
import io.mongock.util.test.Constants;
import org.bson.Document;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class SpringbootRunnerITest {

  private static MongoClient mongoClient;
  private static MongoDbReplicaSet mongodbContainer;
  private static RunnerTestUtil runnerTestUtil;
  private static MongoDatabase database;


  private static MongoCollection<Document> clientsCollection;

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
    database.getCollection("mongockChangeLog").drop();
    database.getCollection(SpringDataAdvanceChangeLog.COLLECTION_NAME).drop();
    database.getCollection(Client.COLLECTION_NAME).drop();
    database.getCollection(SpringDataAdvanceChangeLogWithBeforeFailing.COLLECTION_NAME).drop();
    database.getCollection(SpringDataAdvanceChangeLogWithChangeSetFailing.COLLECTION_NAME).drop();
  }


  @Test
  @DisplayName("SHOULD rollback" +
      "WHEN exception in changeUnit" +
      "IF transaction enabled")
  public void shouldRollBack_WhenExceptionInChangeUnit_IfTransactionEnabled() {

    MongockRunner runner = runnerTestUtil.getRunnerTransactional(FailingChangeLogLegacy.class.getPackage().getName())
        .buildRunner();

    Assertions.assertThrows(MongockException.class, runner::execute);

    // then
    clientsCollection = database.getCollection(Client.COLLECTION_NAME);
    long actual = clientsCollection.countDocuments();
    assertEquals(0, actual);
  }

  @Test
  @DisplayName("SHOULD automatically rollback changeSet and manually before " +
      "WHEN  second changeLog fails at changeSet " +
      "IF strategy is changeLog and  transactional")
  public void shouldNotRollbackFirstChangeLogAndRollbackAutomaticallyChangeSetOfSecondChangeLogAndManuallyBeforeOfSecondChangeLog_whenSecondChangeLogFailAtChangeSet_ifStrategyIsChangeLogAndTransactional() {


    // checks the four rollbacks were called
    SpringDataAdvanceChangeLog.clear();
    SpringDataAdvanceChangeLogWithChangeSetFailing.clear();

    MongockRunner runner = runnerTestUtil.getRunnerTransactional(SpringDataAdvanceChangeLog.class.getName(), SpringDataAdvanceChangeLogWithChangeSetFailing.class.getName())
        .buildRunner();

    MongockException ex = Assertions.assertThrows(MongockException.class, runner::execute);

    assertTrue(ex.getCause().getMessage().contains("Expected exception"));


    Assertions.assertFalse(SpringDataAdvanceChangeLog.rollbackBeforeCalled, "(1)AdvanceChangeLogWithBefore's Rollback before method wasn't executed");
    Assertions.assertFalse(SpringDataAdvanceChangeLog.rollbackCalled, "(2)AdvanceChangeLogWithBefore's Rollback method wasn't executed");

    assertTrue(SpringDataAdvanceChangeLogWithChangeSetFailing.rollbackBeforeCalled, "(3)AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback before method wasn't executed");
    Assertions.assertFalse(SpringDataAdvanceChangeLogWithChangeSetFailing.rollbackCalled, "(4)AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");

    MongoCollection<Document> changeEntryCollection = database.getCollection("mongockChangeLog");
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);
    assertEquals(3, changeEntryList.size());//Only 3, because the changeEntry for the failing changeSet is rolled back

    assertEquals(SpringDataAdvanceChangeLog.class.getSimpleName() + "_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(0).getString("state"));

    assertEquals(SpringDataAdvanceChangeLog.class.getSimpleName(), changeEntryList.get(1).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(1).getString("state"));

    assertEquals(SpringDataAdvanceChangeLogWithChangeSetFailing.class.getSimpleName() + "_before", changeEntryList.get(2).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(2).getString("state"));

    MongoCollection<Document> dataCollection = database.getCollection(SpringDataAdvanceChangeLogWithChangeSetFailing.COLLECTION_NAME);

    FindIterable<Document> clients = dataCollection.find();
    Set<Document> clientsSet = new HashSet<>();
    clients.forEach(clientsSet::add);
    assertEquals(0, clientsSet.size());

  }

  @Test
  @DisplayName("SHOULD rollback Manually only changeSets of last changLog and store change entries " +
      "WHEN  second changeLog fails at changeSet " +
      "IF strategy is migration and non transactional")
  public void shouldRollbackManuallyOnlyChangeSetsOfLastChangelogAndStoreChangeEntry_whenSecondChangeLogFailAtChangeSet_ifStrategyIsMigrationAndNonTransactional() throws InterruptedException {

    // checks the four rollbacks were called
    SpringDataAdvanceChangeLog.clear();
    SpringDataAdvanceChangeLogWithChangeSetFailing.clear();
    MongockRunner runner = runnerTestUtil.getRunner(SpringDataAdvanceChangeLog.class.getName(), SpringDataAdvanceChangeLogWithChangeSetFailing.class.getName())
        .setTransactionStrategy(TransactionStrategy.EXECUTION)
        .buildRunner();

    MongockException ex = Assertions.assertThrows(MongockException.class, runner::execute);


    // checks the four rollbacks were called
    assertTrue(SpringDataAdvanceChangeLog.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS), "AdvanceChangeLogWithBefore's Rollback method wasn't executed");
    assertTrue(SpringDataAdvanceChangeLogWithChangeSetFailing.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS), "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");

    MongoCollection<Document> changeEntryCollection = database.getCollection("mongockChangeLog");
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);

    assertEquals(4, changeEntryList.size());

    assertEquals(SpringDataAdvanceChangeLog.class.getSimpleName() + "_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(0).getString("state"));

    assertEquals(SpringDataAdvanceChangeLog.class.getSimpleName(), changeEntryList.get(1).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(1).getString("state"));

    assertEquals(SpringDataAdvanceChangeLogWithChangeSetFailing.class.getSimpleName() + "_before", changeEntryList.get(2).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(2).getString("state"));

    assertEquals(SpringDataAdvanceChangeLogWithChangeSetFailing.class.getSimpleName(), changeEntryList.get(3).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(3).getString("state"));

  }

  @Test
  @DisplayName("SHOULD rollback Manually only changeSets of last changLog and store change entries " +
      "WHEN  second changeLog fails at changeSet " +
      "IF strategy is changeLog and non transactional")
  public void shouldRollbackManuallyOnlyChangeSetsOfLastChangelogAndStoreChangeEntry_whenSecondChangeLogFailAtChangeSet_ifStrategyIsChangeUnitAndNonTransactional() throws InterruptedException {

    // given
    SpringDataAdvanceChangeLog.clear();
    SpringDataAdvanceChangeLogWithChangeSetFailing.clear();

    MongockRunner runner = runnerTestUtil.getRunner(SpringDataAdvanceChangeLog.class.getName(), SpringDataAdvanceChangeLogWithChangeSetFailing.class.getName())
        .setTransactionStrategy(TransactionStrategy.CHANGE_UNIT)
        .buildRunner();

    MongockException ex = Assertions.assertThrows(MongockException.class, runner::execute);

    // checks the four rollbacks were called
    assertTrue(SpringDataAdvanceChangeLogWithChangeSetFailing.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS), "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");


    MongoCollection<Document> changeEntryCollection = database.getCollection("mongockChangeLog");
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);

    assertEquals(4, changeEntryList.size());

    assertEquals(SpringDataAdvanceChangeLog.class.getSimpleName() + "_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(0).getString("state"));

    assertEquals(SpringDataAdvanceChangeLog.class.getSimpleName(), changeEntryList.get(1).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(1).getString("state"));

    assertEquals(SpringDataAdvanceChangeLogWithChangeSetFailing.class.getSimpleName() + "_before", changeEntryList.get(2).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(2).getString("state"));

    assertEquals(SpringDataAdvanceChangeLogWithChangeSetFailing.class.getSimpleName(), changeEntryList.get(3).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(3).getString("state"));

  }

  @Test
  @DisplayName("SHOULD rollback automatically everything and no changeEntry should be present " +
      "WHEN  second changeLog fails at changeSet " +
      "IF strategy is migration and  transactional")
  public void shouldRollbackAutomaticallyEverythingAndNoChangeEntryShouldBePresent_whenSecondChangeLogFailAtChangeSet_ifStrategyIsMigrationAndTransactional() {

    SpringDataAdvanceChangeLog.clear();
    SpringDataAdvanceChangeLogWithChangeSetFailing.clear();

    MongockRunner runner = runnerTestUtil.getRunnerTransactional(SpringDataAdvanceChangeLog.class.getName(), SpringDataAdvanceChangeLogWithChangeSetFailing.class.getName())
        .setTransactionStrategy(TransactionStrategy.EXECUTION)
        .buildRunner();

    MongockException ex = Assertions.assertThrows(MongockException.class, runner::execute);

    Assertions.assertFalse(SpringDataAdvanceChangeLog.rollbackBeforeCalled, "AdvanceChangeLogWithBefore's Rollback before method wasn't executed");
    Assertions.assertFalse(SpringDataAdvanceChangeLog.rollbackCalled, "AdvanceChangeLogWithBefore's Rollback method wasn't executed");

    Assertions.assertFalse(SpringDataAdvanceChangeLogWithChangeSetFailing.rollbackBeforeCalled, "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback before method wasn't executed");
    Assertions.assertFalse(SpringDataAdvanceChangeLogWithChangeSetFailing.rollbackCalled, "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");

    MongoCollection<Document> changeEntryCollection = database.getCollection("mongockChangeLog");
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);
    assertEquals(0, changeEntryList.size());
  }

  @Test
  @DisplayName("SHOULD not rollback " +
      "WHEN changeSet runs normally " +
      "IF strategy is changeLog and  transactional")
  public void shouldNotRollback_WhenChangeSetRunsNormally_IfStrategyChangeLogAndTransactional() {

    runnerTestUtil.getRunnerTransactional(SpringDataAdvanceChangeLog.class.getName())
        .buildRunner()
        .execute();

    Assertions.assertFalse(SpringDataAdvanceChangeLog.rollbackBeforeCalled, "(1)AdvanceChangeLogWithBefore's Rollback before method wasn't executed");
    Assertions.assertFalse(SpringDataAdvanceChangeLog.rollbackCalled, "(2)AdvanceChangeLogWithBefore's Rollback method wasn't executed");

    MongoCollection<Document> changeEntryCollection = database.getCollection("mongockChangeLog");
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);
    assertEquals(2, changeEntryList.size());

    assertEquals(SpringDataAdvanceChangeLog.class.getSimpleName() + "_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(0).getString("state"));

    assertEquals(SpringDataAdvanceChangeLog.class.getSimpleName(), changeEntryList.get(1).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(1).getString("state"));

    MongoCollection<Document> dataCollection = database.getCollection(SpringDataAdvanceChangeLog.COLLECTION_NAME);
    FindIterable<Document> clients = dataCollection.find();
    Set<Document> clientsSet = new HashSet<>();
    clients.forEach(clientsSet::add);
    assertEquals(11, clientsSet.size());
  }


  @Test
  @DisplayName("SHOULD not run changeSet " +
      "WHEN strategy is changeLog and  transactional " +
      "IF before throws exception")
  public void shouldNotRunChangeSet_WhenStrategyIsChangeLogAndTransactional_IfBeforeThrowsException() {

    SpringDataAdvanceChangeLogWithBeforeFailing.clear();


    MongockRunner runner = runnerTestUtil.getRunnerTransactional(SpringDataAdvanceChangeLogWithBeforeFailing.class.getName())
        .setTransactionStrategy(TransactionStrategy.CHANGE_UNIT)
        .buildRunner();

    MongockException ex = Assertions.assertThrows(MongockException.class, runner::execute);

    assertTrue(SpringDataAdvanceChangeLogWithBeforeFailing.rollbackBeforeCalled, "AdvanceChangeLogWithBeforeFailing's rollback before is expected to be called");
    Assertions.assertFalse(SpringDataAdvanceChangeLogWithBeforeFailing.rollbackCalled, "AdvanceChangeLogWithBeforeFailing's rollback is not expected to be called");
    Assertions.assertFalse(SpringDataAdvanceChangeLogWithBeforeFailing.changeSetCalled, "AdvanceChangeLogWithBeforeFailing's changeSet is not expected to be called");

    MongoCollection<Document> changeEntryCollection = database.getCollection("mongockChangeLog");
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);
    assertEquals(1, changeEntryList.size());

    assertEquals(SpringDataAdvanceChangeLogWithBeforeFailing.class.getSimpleName() + "_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(0).getString("state"));

    MongoCollection<Document> dataCollection = database.getCollection(SpringDataAdvanceChangeLog.COLLECTION_NAME);
    FindIterable<Document> clients = dataCollection.find();
    Set<Document> clientsSet = new HashSet<>();
    clients.forEach(clientsSet::add);
    assertEquals(0, clientsSet.size());
  }

  @Test
  public void shouldCommit_IfTransaction_WhenChangeLogOK() {

    runnerTestUtil.getRunnerTransactional(TransactionSuccessfulChangeLog.class.getName())
        .buildRunner()
        .execute();

    // then
    MongoCollection<Document> dataCollection = database.getCollection(Client.COLLECTION_NAME);
    FindIterable<Document> clients = dataCollection.find();
    Set<Document> clientsSet = new HashSet<>();
    clients.forEach(clientsSet::add);
    assertEquals(10, clientsSet.size());
  }


  @Test
  public void shouldNotExecuteTransaction_IfConfigurationTransactionDisabled() {

    MongockRunner runner = runnerTestUtil.getRunner(FailingChangeLogLegacy.class.getName())
        .setTransactionEnabled(false)
        .buildRunner();
    Assertions.assertThrows(MongockException.class, runner::execute);

    // then
    MongoCollection<Document> dataCollection = database.getCollection(Client.COLLECTION_NAME);
    FindIterable<Document> clients = dataCollection.find();
    Set<Document> clientsSet = new HashSet<>();
    clients.forEach(clientsSet::add);
    assertEquals(10, clientsSet.size());


  }

}
