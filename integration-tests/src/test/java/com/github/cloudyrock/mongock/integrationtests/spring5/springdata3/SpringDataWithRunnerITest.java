package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3;

import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.general.AnotherMongockTestResource;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.general.MongockTestResource;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.withChangockAnnotations.ChangeLogwithChangockAnnotations;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.util.Constants;
import com.github.cloudyrock.springboot.base.MongockApplicationRunner;
import org.bson.Document;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class SpringDataWithRunnerITest extends ApplicationRunnerTestBase {

  private static final String TEST_RESOURCE_CLASSPATH = MongockTestResource.class.getPackage().getName();

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  void shouldExecuteAllChangeSets(String mongoVersion) throws Exception {
    start(mongoVersion);
    // given, then
    getSpringBootBuilderWithSpringData(TEST_RESOURCE_CLASSPATH).buildApplicationRunner().run(null);

    // db changelog collection checking
    long change1 = this.mongoTemplate.getDb().getCollection(Constants.CHANGELOG_COLLECTION_NAME)
        .countDocuments(new Document().append("changeId", "test1").append("author", "testuser"));
    assertEquals(1, change1);
  }

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  void shouldStoreMetadata_WhenChangeSetIsTrack_IfAddedInBuilder(String mongoVersion) throws Exception {
    start(mongoVersion);
    // given
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("string_key", "string_value");
    metadata.put("integer_key", 10);
    metadata.put("float_key", 11.11F);
    metadata.put("double_key", 12.12D);
    metadata.put("long_key", 13L);
    metadata.put("boolean_key", true);

    // then
    getSpringBootBuilderWithSpringData(TEST_RESOURCE_CLASSPATH)
        .withMetadata(metadata)
        .buildApplicationRunner()
        .run(null);

    // then
    Map metadataResult = mongoTemplate.getDb().getCollection(Constants.CHANGELOG_COLLECTION_NAME).find().first().get("metadata", Map.class);
    assertEquals("string_value", metadataResult.get("string_key"));
    assertEquals(10, metadataResult.get("integer_key"));
    assertEquals(11.11F, (Double) metadataResult.get("float_key"), 0.01);
    assertEquals(12.12D, (Double) metadataResult.get("double_key"), 0.01);
    assertEquals(13L, metadataResult.get("long_key"));
    assertEquals(true, metadataResult.get("boolean_key"));

  }

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  void shouldTwoExecutedChangeSet_whenRunningTwice_ifRunAlways(String mongoVersion) throws Exception {
    start(mongoVersion);
    // given
    MongockApplicationRunner runner = getSpringBootBuilderWithSpringData(TEST_RESOURCE_CLASSPATH).buildApplicationRunner();

    // when
    runner.run(null);
    runner.run(null);

    // then
    List<Document> documentList = new ArrayList<>();

    mongoTemplate.getDb().getCollection(Constants.CHANGELOG_COLLECTION_NAME)
        .find(new Document().append("changeSetMethod", "testChangeSetWithAlways").append("state", "EXECUTED"))
        .forEach(documentList::add);

    assertEquals(1, documentList.size());

  }

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  void shouldOneExecutedAndOneIgnoredChangeSet_whenRunningTwice_ifNotRunAlwaysAndTrackIgnore(String mongoVersion) throws Exception {
    start(mongoVersion);
    // given
    getSpringBootBuilderWithSpringData(TEST_RESOURCE_CLASSPATH)
        .setTrackIgnored(true)
        .buildApplicationRunner()
        .run(null);

    // when
    getSpringBootBuilderWithSpringData(TEST_RESOURCE_CLASSPATH)
        .setTrackIgnored(true)
        .buildApplicationRunner()
        .run(null);

    // then
    List<String> stateList = new ArrayList<>();
    mongoTemplate.getDb().getCollection(Constants.CHANGELOG_COLLECTION_NAME)
        .find(new Document()
            .append("changeLogClass", AnotherMongockTestResource.class.getName())
            .append("changeSetMethod", "testChangeSet"))
        .map(document -> document.getString("state"))
        .forEach(stateList::add);
    assertEquals(2, stateList.size());
    assertTrue(stateList.contains("EXECUTED"));
    assertTrue(stateList.contains("IGNORED"));
  }


  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  void shouldOneExecutedAndNoIgnoredChangeSet_whenRunningTwice_ifNotRunAlwaysAndNotTrackIgnore(String mongoVersion) throws Exception {
    start(mongoVersion);
    // given
    MongockApplicationRunner runner = getSpringBootBuilderWithSpringData(TEST_RESOURCE_CLASSPATH)
        .buildApplicationRunner();


    // when
    runner.run(null);
    runner.run(null);

    // then
    List<String> stateList = new ArrayList<>();
    mongoTemplate.getDb().getCollection(Constants.CHANGELOG_COLLECTION_NAME)
        .find(new Document()
            .append("changeLogClass", AnotherMongockTestResource.class.getName())
            .append("changeSetMethod", "testChangeSet"))
        .map(document -> document.getString("state"))
        .forEach(stateList::add);
    assertEquals(1, stateList.size());
    assertTrue(stateList.contains("EXECUTED"));
    assertFalse(stateList.contains("IGNORED"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"mongo:4.2.6"})
  void shouldExecuteChangockAnnotations(String mongoVersion) throws Exception {
    start(mongoVersion);
    // given, then
    getSpringBootBuilderWithSpringData(ChangeLogwithChangockAnnotations.class.getPackage().getName()).buildApplicationRunner().run(null);

    // then
    long changeWithChangockAnnotations = mongoTemplate.getDb().getCollection(Constants.CHANGELOG_COLLECTION_NAME).countDocuments(new Document()
        .append("changeId", "withChangockAnnotations")
        .append("author", "testuser")
        .append("state", "EXECUTED"));
    assertEquals(1, changeWithChangockAnnotations);
  }

  //TODO chenge the following ones to use mongoTemplate and the ChangeLog shouldn'#t use clientSession

//
//
//
//  @ParameterizedTest
//  @ValueSource(strings = {"mongo:4.2.6"})
//  @DisplayName("SHOULD rollback Manually only changeSets of last changLog and store change entries" +
//      "WHEN  second changeLog fails at changeSet" +
//      "IF strategy is migration and non transactional")
//  public void shouldRollbackManuallyOnlyChangeSetsOfLastChangelogAndStoreChangeEntry_whenSecondChangeLogFailAtChangeSet_ifStrategyIsMigrationAndNonTransactional(String mongoVersion) throws InterruptedException {
//    start(mongoVersion);
//
//    // given
//
//    MongoDatabase database = mongoClient.getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);
//
//    MongockConfiguration config = new MongockConfiguration();
//    config.setServiceIdentifier("myService");
//    config.setTrackIgnored(false);
//    config.setChangeLogsScanPackage(Arrays.asList(
//        AdvanceChangeLogWithBefore.class.getName(),
//        AdvanceChangeLogWithBeforeAndChangeSetFailing.class.getName()
//    ));
//    config.setTransactionStrategy(TransactionStrategy.MIGRATION);
//
//    MongockException ex = Assertions.assertThrows(MongockException.class,
//        () -> getStandaloneBuilderWithMongoDBSync4( )
//            .setConfig(config)
//            .setTransactionEnabled(false)
//            .buildRunner()
//            .execute()
//    );
//
//
//    // checks the four rollbacks were called
//    assertTrue(AdvanceChangeLogWithBefore.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS), "AdvanceChangeLogWithBefore's Rollback method wasn't executed");
//    assertTrue(AdvanceChangeLogWithBeforeAndChangeSetFailing.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS), "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");
//
//    MongoCollection<Document> clientCollection = database.getCollection(config.getChangeLogRepositoryName());
//    FindIterable<Document> changeEntryIterator = clientCollection.find();
//    List<Document> changeEntryList = new ArrayList<>();
//    changeEntryIterator.forEach(changeEntryList::add);
//
//    assertEquals(4, changeEntryList.size());
//
//    assertEquals("AdvanceChangeLogWithBefore_before", changeEntryList.get(0).getString("changeId"));
//    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(0).getString("state"));
//
//    assertEquals("AdvanceChangeLogWithBefore", changeEntryList.get(1).getString("changeId"));
//    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(1).getString("state"));
//
//    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing_before", changeEntryList.get(2).getString("changeId"));
//    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(2).getString("state"));
//
//    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing", changeEntryList.get(3).getString("changeId"));
//    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(3).getString("state"));
//
//  }
//
//  @ParameterizedTest
//  @ValueSource(strings = {"mongo:4.2.6"})
//  @DisplayName("SHOULD rollback Manually only changeSets of last changLog and store change entries" +
//      "WHEN  second changeLog fails at changeSet" +
//      "IF strategy is changeLog and non transactional")
//  public void shouldRollbackManuallyOnlyChangeSetsOfLastChangelogAndStoreChangeEntry_whenSecondChangeLogFailAtChangeSet_ifStrategyIsChangeLogAndNonTransactional(String mongoVersion) throws InterruptedException {
//    start(mongoVersion);
//
//    // given
//
//    MongoDatabase database = mongoClient.getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);
//
//    MongockConfiguration config = new MongockConfiguration();
//    config.setServiceIdentifier("myService");
//    config.setTrackIgnored(false);
//    config.setChangeLogsScanPackage(Arrays.asList(
//        AdvanceChangeLogWithBefore.class.getName(),
//        AdvanceChangeLogWithBeforeAndChangeSetFailing.class.getName()
//    ));
//
//    MongockException ex = Assertions.assertThrows(MongockException.class,
//        () -> getStandaloneBuilderWithMongoDBSync4( )
//            .setConfig(config)
//            .setTransactionEnabled(false)
//            .buildRunner()
//            .execute()
//    );
//
//
//    // checks the four rollbacks were called
//    assertTrue(AdvanceChangeLogWithBeforeAndChangeSetFailing.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS), "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");
//
//    MongoCollection<Document> clientCollection = database.getCollection(config.getChangeLogRepositoryName());
//    FindIterable<Document> changeEntryIterator = clientCollection.find();
//    List<Document> changeEntryList = new ArrayList<>();
//    changeEntryIterator.forEach(changeEntryList::add);
//
//    assertEquals(4, changeEntryList.size());
//
//    assertEquals("AdvanceChangeLogWithBefore_before", changeEntryList.get(0).getString("changeId"));
//    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(0).getString("state"));
//
//    assertEquals("AdvanceChangeLogWithBefore", changeEntryList.get(1).getString("changeId"));
//    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(1).getString("state"));
//
//    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing_before", changeEntryList.get(2).getString("changeId"));
//    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(2).getString("state"));
//
//    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing", changeEntryList.get(3).getString("changeId"));
//    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(3).getString("state"));
//
//  }
//
//  @ParameterizedTest
//  @ValueSource(strings = {"mongo:4.2.6"})
//  @DisplayName("SHOULD rollback automatically everything and no changeEntry should be present " +
//      "WHEN  second changeLog fails at changeSet " +
//      "IF strategy is migration and  transactional")
//  public void shouldRollbackAutomaticallyEverythingAndNoChangeEntryShouldBePresent_whenSecondChangeLogFailAtChangeSet_ifStrategyIsMigrationAndTransactional(String mongoVersion) {
//    start(mongoVersion);
//
//    // given
//    MongoDatabase database = mongoClient.getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);
//
//    MongockConfiguration config = new MongockConfiguration();
//    config.setServiceIdentifier("myService");
//    config.setTrackIgnored(false);
//    config.setChangeLogsScanPackage(Arrays.asList(
//        AdvanceChangeLogWithBefore.class.getName(),
//        AdvanceChangeLogWithBeforeAndChangeSetFailing.class.getName()
//    ));
//    config.setTransactionStrategy(TransactionStrategy.MIGRATION);
//
//    MongockException ex = Assertions.assertThrows(MongockException.class,
//        () -> getStandaloneBuilderWithMongoDBSync4( )
//            .setConfig(config)
//            .setTransactionEnabled(true)
//            .buildRunner()
//            .execute()
//    );
//
//
//    // checks the four rollbacks were called
//    Assertions.assertFalse(AdvanceChangeLogWithBefore.rollbackBeforeCalled, "AdvanceChangeLogWithBefore's Rollback before method wasn't executed");
//    Assertions.assertFalse(AdvanceChangeLogWithBefore.rollbackCalled, "AdvanceChangeLogWithBefore's Rollback method wasn't executed");
//
//    Assertions.assertFalse(AdvanceChangeLogWithBeforeAndChangeSetFailing.rollbackBeforeCalled, "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback before method wasn't executed");
//    Assertions.assertFalse(AdvanceChangeLogWithBeforeAndChangeSetFailing.rollbackCalled, "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");
//
//    MongoCollection<Document> clientCollection = database.getCollection(config.getChangeLogRepositoryName());
//    FindIterable<Document> changeEntryIterator = clientCollection.find();
//    List<Document> changeEntryList = new ArrayList<>();
//    changeEntryIterator.forEach(changeEntryList::add);
//    assertEquals(0, changeEntryList.size());
//  }
//
//  @ParameterizedTest
//  @ValueSource(strings = {"mongo:4.2.6"})
//  @DisplayName("SHOULD not rollback first changeLog and rollback automatically changeSet of second changeLog and manually before of second changeLog " +
//      "WHEN  second changeLog fails at changeSet " +
//      "IF strategy is changeLog and  transactional")
//  public void shouldNotRollbackFirstChangeLogAndRollbackAutomaticallyChangeSetOfSecondChangeLogAndManuallyBeforeOfSecondChangeLog_whenSecondChangeLogFailAtChangeSet_ifStrategyIsChangeLogAndTransactional(String mongoVersion) {
//    start(mongoVersion);
//
//    // given
//    MongoDatabase database = mongoClient.getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME);
//
//    MongockConfiguration config = new MongockConfiguration();
//    config.setServiceIdentifier("myService");
//    config.setTrackIgnored(false);
//    config.setChangeLogsScanPackage(Arrays.asList(
//        AdvanceChangeLogWithBefore.class.getName(),
//        AdvanceChangeLogWithBeforeAndChangeSetFailing.class.getName()
//    ));
//
//    MongockException ex = Assertions.assertThrows(MongockException.class,
//        () -> getStandaloneBuilderWithMongoDBSync4( )
//            .setConfig(config)
//            .setTransactionEnabled(true)
//            .buildRunner()
//            .execute()
//    );
//
//
//    // checks the four rollbacks were called
//    Assertions.assertFalse(AdvanceChangeLogWithBefore.rollbackBeforeCalled, "AdvanceChangeLogWithBefore's Rollback before method wasn't executed");
//    Assertions.assertFalse(AdvanceChangeLogWithBefore.rollbackCalled, "AdvanceChangeLogWithBefore's Rollback method wasn't executed");
//
//    Assertions.assertTrue(AdvanceChangeLogWithBeforeAndChangeSetFailing.rollbackBeforeCalled, "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback before method wasn't executed");
//    Assertions.assertFalse(AdvanceChangeLogWithBeforeAndChangeSetFailing.rollbackCalled, "AdvanceChangeLogWithBeforeAndChangeSetFailing's Rollback method wasn't executed");
//
//    MongoCollection<Document> changeEntryCollection = database.getCollection(config.getChangeLogRepositoryName());
//    FindIterable<Document> changeEntryIterator = changeEntryCollection.find();
//    List<Document> changeEntryList = new ArrayList<>();
//    changeEntryIterator.forEach(changeEntryList::add);
//    assertEquals(4, changeEntryList.size());
//
//    assertEquals("AdvanceChangeLogWithBefore_before", changeEntryList.get(0).getString("changeId"));
//    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(0).getString("state"));
//
//    assertEquals("AdvanceChangeLogWithBefore", changeEntryList.get(1).getString("changeId"));
//    assertEquals(ChangeState.EXECUTED.name(), changeEntryList.get(1).getString("state"));
//
//    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing_before", changeEntryList.get(2).getString("changeId"));
//    assertEquals(ChangeState.ROLLED_BACK.name(), changeEntryList.get(2).getString("state"));
//
//    assertEquals("AdvanceChangeLogWithBeforeAndChangeSetFailing", changeEntryList.get(3).getString("changeId"));
//    assertEquals(ChangeState.FAILED.name(), changeEntryList.get(3).getString("state"));
//
//    MongoCollection<Document> dataCollection = database.getCollection(AdvanceChangeLogWithBeforeAndChangeSetFailing.COLLECTION_NAME);
//    FindIterable<Document> clients = dataCollection.find();
//    Set<Document> clientsSet = new HashSet<>();
//    clients.forEach(clientsSet::add);
//    assertEquals(0, clientsSet.size());
//  }

}
