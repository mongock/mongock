package io.mongock.integrationtests.spring5.springdata3;

import io.mongock.driver.api.entry.ChangeState;
import io.mongock.api.exception.MongockException;
import io.mongock.integrationtests.spring5.springdata3.changelogs.client.initializer.ClientInitializerChangeLog;
import io.mongock.integrationtests.spring5.springdata3.changelogs.interfaces.springdata.rollback.SpringDataAdvanceChangeLog;
import io.mongock.integrationtests.spring5.springdata3.changelogs.interfaces.springdata.rollback.SpringDataAdvanceChangeLogWithBeforeFailing;
import io.mongock.integrationtests.spring5.springdata3.changelogs.interfaces.springdata.rollback.SpringDataAdvanceChangeLogWithChangeSetFailing;
import io.mongock.integrationtests.spring5.springdata3.changelogs.transaction.commitNonFailFast.CommitNonFailFastChangeLog;
import io.mongock.integrationtests.spring5.springdata3.changelogs.transaction.rollback.RollbackChangeLog;
import io.mongock.integrationtests.spring5.springdata3.changelogs.transaction.successful.TransactionSuccessfulChangeLog;
import io.mongock.integrationtests.spring5.springdata3.client.ClientRepository;
import io.mongock.integrationtests.spring5.springdata3.util.MongoContainer;


import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.mongock.runner.springboot.base.MongockApplicationRunner;
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// TODO add methodSources to automatize parametrization

@Testcontainers
class SpringApplicationITest {

  private ConfigurableApplicationContext ctx;


  @AfterEach
  void closingSpringApp() {
    if (ctx != null) {
      ctx.close();
      await().atMost(1, TimeUnit.MINUTES)
          .pollInterval(1, TimeUnit.SECONDS)
          .until(() -> !ctx.isActive());
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6", "mongo:3.6.3"})
  void SpringApplicationShouldRunChangeLogs(String mongoVersion) {
    ctx = RuntimeTestUtil.startSpringAppWithMongoDbVersionAndDefaultPackage(mongoVersion);
    assertEquals(ClientInitializerChangeLog.INITIAL_CLIENTS, ctx.getBean(ClientRepository.class).count());
  }


  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  void ApplicationRunnerShouldBeInjected(String mongoVersion) {
    ctx = RuntimeTestUtil.startSpringAppWithMongoDbVersionAndDefaultPackage(mongoVersion);
    ctx.getBean(MongockApplicationRunner.class);
  }


  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  void ApplicationRunnerShouldNotBeInjected_IfDisabledByProperties(String mongoVersion) {
    Map<String, String> parameters = new HashMap<>();
    parameters.put("mongock.enabled", "false");
    parameters.put("mongock.changeLogsScanPackage", "io.mongock.integrationtests.spring5.springdata3.changelogs.client");
    parameters.put("mongock.transactionable", "false");
    ctx = RuntimeTestUtil.startSpringAppWithMongoDbVersionAndParameters(mongoVersion, parameters);
    Exception ex = assertThrows(
        NoSuchBeanDefinitionException.class,
        () -> ctx.getBean(MongockApplicationRunner.class));
    assertEquals(
        "No qualifying bean of type 'io.mongock.runner.springboot.base.MongockApplicationRunner' available",
        ex.getMessage()
    );
  }


  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  void InitializingBeanShouldNotBeInjected(String mongoVersion) {
    ctx = RuntimeTestUtil.startSpringAppWithMongoDbVersionAndDefaultPackage(mongoVersion);
    Exception ex = assertThrows(
        NoSuchBeanDefinitionException.class,
        () -> ctx.getBean(MongockInitializingBeanRunner.class),
        "MongockInitializingBeanRunner should not be injected to the context as runner-type is not set");
    assertEquals(
        "No qualifying bean of type 'io.mongock.runner.springboot.base.MongockInitializingBeanRunner' available",
        ex.getMessage()
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  void shouldThrowExceptionWhenScanPackageNotSpecified(String mongoVersion) {
    Exception ex = assertThrows(
        BeanCreationException.class,
        () -> RuntimeTestUtil.startSpringAppWithMongoDbVersionAndNoPackage(mongoVersion));
    Throwable BeanInstantiationEx = ex.getCause();
    assertEquals(BeanInstantiationException.class, BeanInstantiationEx.getClass());
    Throwable mongockEx = BeanInstantiationEx.getCause();
    assertEquals(MongockException.class, mongockEx.getClass());
    assertEquals("Scan package for changeLogs is not set: use appropriate setter", mongockEx.getMessage());
  }

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  void shouldRollBack_IfTransaction_WhenExceptionInChangeLog(String mongoDbVersion) {
    MongoContainer mongoContainer = RuntimeTestUtil.startMongoDbContainer(mongoDbVersion);
    MongoCollection clientsCollection = MongoClients.create(mongoContainer.getReplicaSetUrl()).getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME).getCollection(Mongock4Spring5SpringData3App.CLIENTS_COLLECTION_NAME);
    try {
      Map<String, String> parameters = new HashMap<>();
      parameters.put("mongock.changeLogsScanPackage", RollbackChangeLog.class.getPackage().getName());
      ctx = RuntimeTestUtil.startSpringAppWithParameters(mongoContainer, parameters);
    } catch (Exception ex) {
      //ignore
    }

    // then
    long actual = clientsCollection.countDocuments();
    assertEquals(0, actual);
  }

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  @DisplayName("SHOULD automatically rollback changeSet and manually before " +
      "WHEN  second changeLog fails at changeSet " +
      "IF strategy is changeLog and  transactional")
  public void shouldNotRollbackFirstChangeLogAndRollbackAutomaticallyChangeSetOfSecondChangeLogAndManuallyBeforeOfSecondChangeLog_whenSecondChangeLogFailAtChangeSet_ifStrategyIsChangeLogAndTransactional(String mongoVersion) {
    // given
    MongoContainer mongoContainer = RuntimeTestUtil.startMongoDbContainer(mongoVersion);
    MongoDatabase database = MongoClients.create(mongoContainer.getReplicaSetUrl()).getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);


    // checks the four rollbacks were called
    SpringDataAdvanceChangeLog.clear();
    SpringDataAdvanceChangeLogWithChangeSetFailing.clear();
    Map<String, String> parameters = new HashMap<>();
    parameters.put("mongock.changeLogsScanPackage", String.format("%s,%s", SpringDataAdvanceChangeLog.class.getName(), SpringDataAdvanceChangeLogWithChangeSetFailing.class.getName()));
    IllegalStateException  ex = Assertions.assertThrows(IllegalStateException.class,
        () -> ctx = RuntimeTestUtil.startSpringAppWithParameters(mongoContainer, parameters)
    );

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

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  @DisplayName("SHOULD rollback Manually only changeSets of last changLog and store change entries " +
      "WHEN  second changeLog fails at changeSet " +
      "IF strategy is migration and non transactional")
  public void shouldRollbackManuallyOnlyChangeSetsOfLastChangelogAndStoreChangeEntry_whenSecondChangeLogFailAtChangeSet_ifStrategyIsMigrationAndNonTransactional(String mongoVersion) throws InterruptedException {
      MongoContainer mongoContainer = RuntimeTestUtil.startMongoDbContainer(mongoVersion);
      MongoDatabase database = MongoClients.create(mongoContainer.getReplicaSetUrl()).getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);

    // checks the four rollbacks were called
      SpringDataAdvanceChangeLog.clear();
      SpringDataAdvanceChangeLogWithChangeSetFailing.clear();
      Map<String, String> parameters = new HashMap<>();
      parameters.put("mongock.transactionStrategy", "MIGRATION");
      parameters.put("mongock.transactionEnabled", "false");
      parameters.put("mongock.changeLogsScanPackage", String.format("%s,%s", SpringDataAdvanceChangeLog.class.getName(), SpringDataAdvanceChangeLogWithChangeSetFailing.class.getName()));
      IllegalStateException  ex = Assertions.assertThrows(IllegalStateException.class,
          () -> ctx = RuntimeTestUtil.startSpringAppWithParameters(mongoContainer, parameters)
      );


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

    @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  @DisplayName("SHOULD rollback Manually only changeSets of last changLog and store change entries " +
      "WHEN  second changeLog fails at changeSet " +
      "IF strategy is changeLog and non transactional")
  public void shouldRollbackManuallyOnlyChangeSetsOfLastChangelogAndStoreChangeEntry_whenSecondChangeLogFailAtChangeSet_ifStrategyIsChangeLogAndNonTransactional(String mongoVersion) throws InterruptedException {
      MongoContainer mongoContainer = RuntimeTestUtil.startMongoDbContainer(mongoVersion);
      MongoDatabase database = MongoClients.create(mongoContainer.getReplicaSetUrl()).getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);

    // given
      SpringDataAdvanceChangeLog.clear();
      SpringDataAdvanceChangeLogWithChangeSetFailing.clear();
      Map<String, String> parameters = new HashMap<>();
      parameters.put("mongock.transactionEnabled", "false");
      parameters.put("mongock.changeLogsScanPackage", String.format("%s,%s", SpringDataAdvanceChangeLog.class.getName(), SpringDataAdvanceChangeLogWithChangeSetFailing.class.getName()));
      IllegalStateException  ex = Assertions.assertThrows(IllegalStateException.class,
          () -> ctx = RuntimeTestUtil.startSpringAppWithParameters(mongoContainer, parameters)
      );

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



   
  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  @DisplayName("SHOULD rollback automatically everything and no changeEntry should be present " +
      "WHEN  second changeLog fails at changeSet " +
      "IF strategy is migration and  transactional")
  public void shouldRollbackAutomaticallyEverythingAndNoChangeEntryShouldBePresent_whenSecondChangeLogFailAtChangeSet_ifStrategyIsMigrationAndTransactional(String mongoVersion) {
    MongoContainer mongoContainer = RuntimeTestUtil.startMongoDbContainer(mongoVersion);
    MongoDatabase database = MongoClients.create(mongoContainer.getReplicaSetUrl()).getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);

    SpringDataAdvanceChangeLog.clear();
    SpringDataAdvanceChangeLogWithChangeSetFailing.clear();
    Map<String, String> parameters = new HashMap<>();
    parameters.put("mongock.transactionStrategy", "MIGRATION");
    parameters.put("mongock.changeLogsScanPackage", String.format("%s,%s", SpringDataAdvanceChangeLog.class.getName(), SpringDataAdvanceChangeLogWithChangeSetFailing.class.getName()));
    IllegalStateException  ex = Assertions.assertThrows(IllegalStateException.class,
        () -> ctx = RuntimeTestUtil.startSpringAppWithParameters(mongoContainer, parameters)
    );
    
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

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  @DisplayName("SHOULD not rollback " +
      "WHEN changeSet runs normally " +
      "IF strategy is changeLog and  transactional")
  public void shouldNotRollback_WhenChangeSetRunsNormally_IfStrategyChangeLogAndTransactional(String mongoVersion) {
    MongoContainer mongoContainer = RuntimeTestUtil.startMongoDbContainer(mongoVersion);
    MongoDatabase database = MongoClients.create(mongoContainer.getReplicaSetUrl()).getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);

    SpringDataAdvanceChangeLog.clear();
    Map<String, String> parameters = new HashMap<>();
    parameters.put("mongock.changeLogsScanPackage",SpringDataAdvanceChangeLog.class.getName());
    ctx = RuntimeTestUtil.startSpringAppWithParameters(mongoContainer, parameters);


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

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  @DisplayName("SHOULD not run changeSet " +
      "WHEN strategy is changeLog and  transactional " +
      "IF before throws exception")
  public void shouldNotRunChangeSet_WhenStrategyIsChangeLogAndTransactional_IfBeforeThrowsException(String mongoVersion) {
    MongoContainer mongoContainer = RuntimeTestUtil.startMongoDbContainer(mongoVersion);
    MongoDatabase database = MongoClients.create(mongoContainer.getReplicaSetUrl()).getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);

    SpringDataAdvanceChangeLog.clear();
    SpringDataAdvanceChangeLogWithChangeSetFailing.clear();
    Map<String, String> parameters = new HashMap<>();
    parameters.put("mongock.changeLogsScanPackage", SpringDataAdvanceChangeLogWithBeforeFailing.class.getName());
    IllegalStateException  ex = Assertions.assertThrows(IllegalStateException.class,
        () -> ctx = RuntimeTestUtil.startSpringAppWithParameters(mongoContainer, parameters)
    );

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

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  void shouldCommit_IfTransaction_WhenChangeLogOK(String mongoDbVersion) {
    ctx = RuntimeTestUtil.startSpringAppWithMongoDbVersionAndPackage(mongoDbVersion, TransactionSuccessfulChangeLog.class.getPackage().getName());

    // then
    assertEquals(10, ctx.getBean(ClientRepository.class).count());
  }

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  void shouldCommit_IfChangeLogFail_WhenNonFailFast(String mongoDbVersion) {
    ctx = RuntimeTestUtil.startSpringAppWithMongoDbVersionAndPackage(mongoDbVersion, CommitNonFailFastChangeLog.class.getPackage().getName());

    // then
    assertEquals(10, ctx.getBean(ClientRepository.class).count());
  }

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  void shouldNotExecuteTransaction_IfConfigurationTransactionDisabled(String mongoDbVersion) {
    ctx = RuntimeTestUtil.startSpringAppWithTransactionDisabledMongoDbVersionAndPackage(mongoDbVersion, CommitNonFailFastChangeLog.class.getPackage().getName());

    // then
    assertEquals(10, ctx.getBean(ClientRepository.class).count());
  }


}
