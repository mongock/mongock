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
import io.mongock.runner.springboot.migration.FailingChangeLog;
import io.mongock.runner.springboot.migration.RunAlwaysSuccessfulChangeUnit;
import io.mongock.runner.springboot.migration.SpringDataAdvanceChangeUnit;
import io.mongock.runner.springboot.migration.SpringDataAdvanceWithBeforeFailingChangeUnit;
import io.mongock.runner.springboot.migration.SpringDataAdvanceWithChangeSetFailingChangeUnit;
import io.mongock.runner.springboot.migration.TransactionSuccessfulChangeUnit;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    database.getCollection(SpringDataAdvanceChangeUnit.COLLECTION_NAME).drop();
    database.getCollection(Client.COLLECTION_NAME).drop();
    database.getCollection(SpringDataAdvanceWithBeforeFailingChangeUnit.COLLECTION_NAME).drop();
    database.getCollection(SpringDataAdvanceWithChangeSetFailingChangeUnit.COLLECTION_NAME).drop();
  }


  @Test
  @DisplayName("SHOULD rollback" +
      "WHEN exception in changeUnit" +
      "IF transaction enabled")
  public void shouldRollBack_WhenExceptionInChangeUnit_IfTransactionEnabled() {

    MongockRunner runner = runnerTestUtil.getRunnerTransactional(FailingChangeLog.class.getPackage().getName())
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
    SpringDataAdvanceChangeUnit.clear();
    SpringDataAdvanceWithChangeSetFailingChangeUnit.clear();

    MongockRunner runner = runnerTestUtil.getRunnerTransactional(SpringDataAdvanceChangeUnit.class.getName(), SpringDataAdvanceWithChangeSetFailingChangeUnit.class.getName())
        .buildRunner();

    MongockException ex = Assertions.assertThrows(MongockException.class, runner::execute);

    assertTrue(ex.getCause().getMessage().contains("Expected exception"));


    Assertions.assertFalse(SpringDataAdvanceChangeUnit.rollbackBeforeCalled, "(1)AdvanceChangeLogWithBefore's Rollback before method wasn't executed");
    Assertions.assertFalse(SpringDataAdvanceChangeUnit.rollbackCalled, "(2)AdvanceChangeLogWithBefore's Rollback method wasn't executed");

    assertTrue(SpringDataAdvanceWithChangeSetFailingChangeUnit.rollbackBeforeCalled, "(3)AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback before method wasn't executed");
    Assertions.assertFalse(SpringDataAdvanceWithChangeSetFailingChangeUnit.rollbackCalled, "(4)AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");

    MongoCollection<Document> changeEntryCollection = database.getCollection("mongockChangeLog");
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);
    assertEquals(3, changeEntryList.size());//Only 3, because the changeEntry for the failing changeSet is rolled back

    assertEquals(SpringDataAdvanceChangeUnit.class.getSimpleName() + "_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(0).getString("state"));

    assertEquals(SpringDataAdvanceChangeUnit.class.getSimpleName(), changeEntryList.get(1).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(1).getString("state"));

    assertEquals(SpringDataAdvanceWithChangeSetFailingChangeUnit.class.getSimpleName() + "_before", changeEntryList.get(2).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(2).getString("state"));

    MongoCollection<Document> dataCollection = database.getCollection(SpringDataAdvanceWithChangeSetFailingChangeUnit.COLLECTION_NAME);

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
    SpringDataAdvanceChangeUnit.clear();
    SpringDataAdvanceWithChangeSetFailingChangeUnit.clear();
    MongockRunner runner = runnerTestUtil.getRunner(SpringDataAdvanceChangeUnit.class.getName(), SpringDataAdvanceWithChangeSetFailingChangeUnit.class.getName())
        .setTransactionStrategy(TransactionStrategy.EXECUTION)
        .buildRunner();

    MongockException ex = Assertions.assertThrows(MongockException.class, runner::execute);


    // checks the four rollbacks were called
    assertTrue(SpringDataAdvanceChangeUnit.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS), "AdvanceChangeLogWithBefore's Rollback method wasn't executed");
    assertTrue(SpringDataAdvanceWithChangeSetFailingChangeUnit.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS), "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");

    MongoCollection<Document> changeEntryCollection = database.getCollection("mongockChangeLog");
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);

    assertEquals(4, changeEntryList.size());

    assertEquals(SpringDataAdvanceChangeUnit.class.getSimpleName() + "_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(0).getString("state"));

    assertEquals(SpringDataAdvanceChangeUnit.class.getSimpleName(), changeEntryList.get(1).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(1).getString("state"));

    assertEquals(SpringDataAdvanceWithChangeSetFailingChangeUnit.class.getSimpleName() + "_before", changeEntryList.get(2).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(2).getString("state"));

    assertEquals(SpringDataAdvanceWithChangeSetFailingChangeUnit.class.getSimpleName(), changeEntryList.get(3).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(3).getString("state"));

  }

  @Test
  @DisplayName("SHOULD rollback Manually only changeSets of last changLog and store change entries " +
      "WHEN  second changeLog fails at changeSet " +
      "IF strategy is changeLog and non transactional")
  public void shouldRollbackManuallyOnlyChangeSetsOfLastChangelogAndStoreChangeEntry_whenSecondChangeLogFailAtChangeSet_ifStrategyIsChangeUnitAndNonTransactional() throws InterruptedException {

    // given
    SpringDataAdvanceChangeUnit.clear();
    SpringDataAdvanceWithChangeSetFailingChangeUnit.clear();

    MongockRunner runner = runnerTestUtil.getRunner(SpringDataAdvanceChangeUnit.class.getName(), SpringDataAdvanceWithChangeSetFailingChangeUnit.class.getName())
        .setTransactionStrategy(TransactionStrategy.CHANGE_UNIT)
        .buildRunner();

    MongockException ex = Assertions.assertThrows(MongockException.class, runner::execute);

    // checks the four rollbacks were called
    assertTrue(SpringDataAdvanceWithChangeSetFailingChangeUnit.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS), "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");


    MongoCollection<Document> changeEntryCollection = database.getCollection("mongockChangeLog");
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);

    assertEquals(4, changeEntryList.size());

    assertEquals(SpringDataAdvanceChangeUnit.class.getSimpleName() + "_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(0).getString("state"));

    assertEquals(SpringDataAdvanceChangeUnit.class.getSimpleName(), changeEntryList.get(1).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(1).getString("state"));

    assertEquals(SpringDataAdvanceWithChangeSetFailingChangeUnit.class.getSimpleName() + "_before", changeEntryList.get(2).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(2).getString("state"));

    assertEquals(SpringDataAdvanceWithChangeSetFailingChangeUnit.class.getSimpleName(), changeEntryList.get(3).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(3).getString("state"));

  }

  @Test
  @DisplayName("SHOULD rollback automatically everything and no changeEntry should be present " +
      "WHEN  second changeLog fails at changeSet " +
      "IF strategy is migration and  transactional")
  public void shouldRollbackAutomaticallyEverythingAndNoChangeEntryShouldBePresent_whenSecondChangeLogFailAtChangeSet_ifStrategyIsMigrationAndTransactional() {

    SpringDataAdvanceChangeUnit.clear();
    SpringDataAdvanceWithChangeSetFailingChangeUnit.clear();

    MongockRunner runner = runnerTestUtil.getRunnerTransactional(SpringDataAdvanceChangeUnit.class.getName(), SpringDataAdvanceWithChangeSetFailingChangeUnit.class.getName())
        .setTransactionStrategy(TransactionStrategy.EXECUTION)
        .buildRunner();

    MongockException ex = Assertions.assertThrows(MongockException.class, runner::execute);

    Assertions.assertFalse(SpringDataAdvanceChangeUnit.rollbackBeforeCalled, "AdvanceChangeLogWithBefore's Rollback before method wasn't executed");
    Assertions.assertFalse(SpringDataAdvanceChangeUnit.rollbackCalled, "AdvanceChangeLogWithBefore's Rollback method wasn't executed");

    Assertions.assertFalse(SpringDataAdvanceWithChangeSetFailingChangeUnit.rollbackBeforeCalled, "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback before method wasn't executed");
    Assertions.assertFalse(SpringDataAdvanceWithChangeSetFailingChangeUnit.rollbackCalled, "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");

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

    runnerTestUtil.getRunnerTransactional(SpringDataAdvanceChangeUnit.class.getName())
        .buildRunner()
        .execute();

    Assertions.assertFalse(SpringDataAdvanceChangeUnit.rollbackBeforeCalled, "(1)AdvanceChangeLogWithBefore's Rollback before method wasn't executed");
    Assertions.assertFalse(SpringDataAdvanceChangeUnit.rollbackCalled, "(2)AdvanceChangeLogWithBefore's Rollback method wasn't executed");

    MongoCollection<Document> changeEntryCollection = database.getCollection("mongockChangeLog");
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);
    assertEquals(2, changeEntryList.size());

    assertEquals(SpringDataAdvanceChangeUnit.class.getSimpleName() + "_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(0).getString("state"));

    assertEquals(SpringDataAdvanceChangeUnit.class.getSimpleName(), changeEntryList.get(1).getString("changeId"));
    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(1).getString("state"));

    MongoCollection<Document> dataCollection = database.getCollection(SpringDataAdvanceChangeUnit.COLLECTION_NAME);
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

    SpringDataAdvanceWithBeforeFailingChangeUnit.clear();


    MongockRunner runner = runnerTestUtil.getRunnerTransactional(SpringDataAdvanceWithBeforeFailingChangeUnit.class.getName())
        .setTransactionStrategy(TransactionStrategy.CHANGE_UNIT)
        .buildRunner();

    MongockException ex = Assertions.assertThrows(MongockException.class, runner::execute);

    assertTrue(SpringDataAdvanceWithBeforeFailingChangeUnit.rollbackBeforeCalled, "AdvanceChangeLogWithBeforeFailing's rollback before is expected to be called");
    Assertions.assertFalse(SpringDataAdvanceWithBeforeFailingChangeUnit.rollbackCalled, "AdvanceChangeLogWithBeforeFailing's rollback is not expected to be called");
    Assertions.assertFalse(SpringDataAdvanceWithBeforeFailingChangeUnit.changeSetCalled, "AdvanceChangeLogWithBeforeFailing's changeSet is not expected to be called");

    MongoCollection<Document> changeEntryCollection = database.getCollection("mongockChangeLog");
    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
    List<Document> changeEntryList = new ArrayList<>();
    changeEntryIterator.forEach(changeEntryList::add);
    assertEquals(1, changeEntryList.size());

    assertEquals(SpringDataAdvanceWithBeforeFailingChangeUnit.class.getSimpleName() + "_before", changeEntryList.get(0).getString("changeId"));
    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(0).getString("state"));

    MongoCollection<Document> dataCollection = database.getCollection(SpringDataAdvanceChangeUnit.COLLECTION_NAME);
    FindIterable<Document> clients = dataCollection.find();
    Set<Document> clientsSet = new HashSet<>();
    clients.forEach(clientsSet::add);
    assertEquals(0, clientsSet.size());
  }

  @Test
  public void shouldCommit_IfTransaction_WhenChangeLogOK() {

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("string_key", "string_value");
    metadata.put("integer_key", 10);
    metadata.put("float_key", 11.11F);
    metadata.put("double_key", 12.12D);
    metadata.put("long_key", 13L);
    metadata.put("boolean_key", true);

    runnerTestUtil.getRunnerTransactional(TransactionSuccessfulChangeUnit.class.getName())
        .withMetadata(metadata)
        .buildRunner()
        .execute();

    // then
    MongoCollection<Document> dataCollection = database.getCollection(Client.COLLECTION_NAME);
    FindIterable<Document> clients = dataCollection.find();
    Set<Document> clientsSet = new HashSet<>();
    clients.forEach(clientsSet::add);
    assertEquals(10, clientsSet.size());

    // checking changeEntries
    MongoCollection<Document> changeEntryCollection = database.getCollection(Constants.CHANGELOG_COLLECTION_NAME);
    assertEquals(2, changeEntryCollection.countDocuments());

    FindIterable<Document> docs = changeEntryCollection.find();

    Document changeEntryExecution = changeEntryCollection.find(new Document().append("changeId", TransactionSuccessfulChangeUnit.class.getSimpleName()).append("author", "mongock"))
        .first();
    assertNotNull(changeEntryExecution);
    Document changeEntryBefore = changeEntryCollection.find(new Document().append("changeId", TransactionSuccessfulChangeUnit.class.getSimpleName() + "_before").append("author", "mongock"))
        .first();
    assertNotNull(changeEntryBefore);


    checkMetadata(changeEntryExecution.get("metadata", Map.class));
    checkMetadata(changeEntryBefore.get("metadata", Map.class));

  }


  @Test
  void shouldJustOneExecutedChangeEntry_whenRunAlways_ifExecutedTwice() {

    // when
    runnerTestUtil.getRunnerTransactional(RunAlwaysSuccessfulChangeUnit.class.getName())
        .buildRunner()
        .execute();
    runnerTestUtil.getRunnerTransactional(RunAlwaysSuccessfulChangeUnit.class.getName())
        .buildRunner()
        .execute();

    // then
    List<Document> documentList = new ArrayList<>();

    database.getCollection(Constants.CHANGELOG_COLLECTION_NAME)
        .find(new Document().append("changeId", RunAlwaysSuccessfulChangeUnit.class.getSimpleName()))
        .forEach(documentList::add);

    assertEquals(1, documentList.size());
    assertEquals("EXECUTED", documentList.get(0).get("state"));

  }

  @Test
  public void shouldNotExecuteTransaction_IfConfigurationTransactionDisabled() {

    MongockRunner runner = runnerTestUtil.getRunner(FailingChangeLog.class.getName())
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

  private void checkMetadata(Map metadataResult) {
    assertEquals("string_value", metadataResult.get("string_key"));
    assertEquals(10, metadataResult.get("integer_key"));
    assertEquals(11.11F, (Double) metadataResult.get("float_key"), 0.01);
    assertEquals(12.12D, (Double) metadataResult.get("double_key"), 0.01);
    assertEquals(13L, metadataResult.get("long_key"));
    assertEquals(true, metadataResult.get("boolean_key"));
  }

}
