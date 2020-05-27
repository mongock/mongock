package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.driver;


import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.driver.changelogs.integration.test1.withnorunalways.ChangeLogSuccess;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.driver.changelogs.integration.test1.withrunalways.WithRunAlways;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.driver.changelogs.integration.test2.ChangeLogFailure;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.driver.changelogs.integration.test3.ChangeLogEnsureDecorator;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.driver.util.CallVerifier;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.driver.util.CallVerifierImpl;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.driver.util.IntegrationTestBase;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import io.changock.driver.api.entry.ChangeState;
import io.changock.migration.api.annotations.ChangeSet;
import io.changock.migration.api.exception.ChangockException;
import io.changock.runner.standalone.TestChangockRunner;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class MongoDriverITest extends IntegrationTestBase {

  private static final String CHANGELOG_COLLECTION_NAME = "changockChangeLog";
  private static final String KEY_EXECUTION_ID = "executionId";
  private static final String KEY_CHANGE_ID = "changeId";
  private static final String KEY_AUTHOR = "author";
  private static final String KEY_STATE = "state";
  private static final String KEY_TIMESTAMP = "timestamp";
  private static final String KEY_CHANGE_LOG_CLASS = "changeLogClass";
  private static final String KEY_CHANGE_SET_METHOD = "changeSetMethod";
  private static final String KEY_EXECUTION_MILLIS = "executionMillis";
  private static final String KEY_METADATA = "metadata";
  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void shouldRunAllChangeLogsSuccessfully() {
    collection = this.getDataBase().getCollection(CHANGELOG_COLLECTION_NAME);
    runChanges(getDriver(), ChangeLogSuccess.class, CHANGELOG_COLLECTION_NAME, Collections.emptyList());
  }



  @Test
  public void shouldRegisterChangeSetAsIgnored_WhenAlreadyExecuted_IfNotRunAlways() throws NoSuchMethodException {
    collection = this.getDataBase().getCollection(CHANGELOG_COLLECTION_NAME);
    collection.insertOne(getChangeEntryDocument(ChangeLogSuccess.class.getMethod("method_0"), ChangeState.EXECUTED));
    runChanges(getDriver(),ChangeLogSuccess.class, CHANGELOG_COLLECTION_NAME, Collections.singletonList("method_0"));
  }

  @Test
  public void shouldRegisterChangeSetAsExecuted_WhenAlreadyExecuted_IfRunAlways() throws NoSuchMethodException {
    collection = this.getDataBase().getCollection(CHANGELOG_COLLECTION_NAME);
    collection.insertOne(getChangeEntryDocument(WithRunAlways.class.getMethod("method_0"), ChangeState.EXECUTED));
    runChanges(getDriver(), WithRunAlways.class, CHANGELOG_COLLECTION_NAME);
  }

  @Test
  public void shouldRegisterChangeSetAsExecuted_WhenAlreadyIgnored_IfNotRunAlways() throws NoSuchMethodException {
    collection = this.getDataBase().getCollection(CHANGELOG_COLLECTION_NAME);
    collection.insertOne(getChangeEntryDocument(ChangeLogSuccess.class.getMethod("method_0"), ChangeState.IGNORED));
    runChanges(getDriver(), ChangeLogSuccess.class, CHANGELOG_COLLECTION_NAME);
  }

  @Test
  public void shouldRegisterChangeSetAsExecuted_WhenAlreadyIgnored_IfRunAlways() throws NoSuchMethodException {
    collection = this.getDataBase().getCollection(CHANGELOG_COLLECTION_NAME);
    collection.insertOne(getChangeEntryDocument(WithRunAlways.class.getMethod("method_0"), ChangeState.IGNORED));
    runChanges(getDriver(), WithRunAlways.class, CHANGELOG_COLLECTION_NAME);
  }

  @Test
  public void shouldRegisterChangeSetAsExecuted_WhenAlreadyFailed_IfNotRunAlways() throws NoSuchMethodException {
    collection = this.getDataBase().getCollection(CHANGELOG_COLLECTION_NAME);
    collection.insertOne(getChangeEntryDocument(ChangeLogSuccess.class.getMethod("method_0"), ChangeState.FAILED));
    runChanges(getDriver(), ChangeLogSuccess.class, CHANGELOG_COLLECTION_NAME);
  }

  @Test
  public void shouldRegisterChangeSetAsExecuted_WhenAlreadyFailed_IfRunAlways() throws NoSuchMethodException {
    collection = this.getDataBase().getCollection(CHANGELOG_COLLECTION_NAME);
    collection.insertOne(getChangeEntryDocument(WithRunAlways.class.getMethod("method_0"), ChangeState.FAILED));
    runChanges(getDriver(), WithRunAlways.class, CHANGELOG_COLLECTION_NAME);
  }


  @Test
  public void shouldUseDifferentChangeLogCollectionName_whenSettingChangeLogCollectionName() {
    String newChangeLogCollectionName = "newChangeLogCollectionName";
    collection = this.getDataBase().getCollection(newChangeLogCollectionName);
    MongoSync4Driver driver = new MongoSync4Driver(this.getDataBase());
    driver.setChangeLogCollectionName(newChangeLogCollectionName);
    runChanges(driver, ChangeLogSuccess.class, newChangeLogCollectionName, Collections.emptyList());
  }

  private Document getChangeEntryDocument(Method method, ChangeState state) {
    ChangeSet changeSet = method.getAnnotation(ChangeSet.class);
    return new Document()
        .append(KEY_EXECUTION_ID, "any")
        .append(KEY_CHANGE_ID, changeSet.id())
        .append(KEY_AUTHOR, changeSet.author())
        .append(KEY_STATE, state.name())
        .append(KEY_TIMESTAMP, new Date())
        .append(KEY_CHANGE_LOG_CLASS, method.getDeclaringClass().getName())
        .append(KEY_CHANGE_SET_METHOD, method.getName())
        .append(KEY_EXECUTION_MILLIS, 0L)
        .append(KEY_METADATA, getStringObjectMap());
  }

  private void runChanges(MongoSync4Driver driver, Class changeLogClass, String changeLogCollectionName) {
    runChanges(driver, changeLogClass, changeLogCollectionName, Collections.emptyList());
  }
  private void runChanges(MongoSync4Driver driver, Class changeLogClass, String chageLogCollectionName, Collection<String> ignoredChangeIds) {
    Map<String, Object> metadata = getStringObjectMap();

    String executionId = UUID.randomUUID().toString();
    TestChangockRunner runner = TestChangockRunner.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(changeLogClass.getPackage().getName())
        .withMetadata(metadata)
        .setExecutionId(executionId)
        .build();
    runner.execute();

    collection = this.getDataBase().getCollection(chageLogCollectionName);

    FindIterable<Document> documents = collection.find(new Document());
//        .append(KEY_EXECUTION_ID, executionId));
    MongoCursor<Document> iterator = documents.iterator();
    List<Document> documentList = new ArrayList<>();
    while (iterator.hasNext()) {
      Document next = iterator.next();
      documentList.add(next);
    }

    for (int i = 0; i < 5; i++) {
      Document change = collection.find(new Document()
          .append(KEY_EXECUTION_ID, executionId)
          .append(KEY_AUTHOR, "testuser")
          .append(KEY_CHANGE_ID, "method_" + i)).first();
      String executionIdChange = change.get(KEY_EXECUTION_ID, String.class);
      String changeId = change.get(KEY_CHANGE_ID, String.class);
      String author = change.get(KEY_AUTHOR, String.class);
      String state = change.get(KEY_STATE, String.class);
      Date timestamp = change.get(KEY_TIMESTAMP, Date.class);
      String changeLogClassInstance = change.get(KEY_CHANGE_LOG_CLASS, String.class);
      String changeSetMethod = change.get(KEY_CHANGE_SET_METHOD, String.class);
      Long executionMillis = change.get(KEY_EXECUTION_MILLIS, Long.class);
      Map metadataResult = change.get(KEY_METADATA, Map.class);
      assertNotNull(executionIdChange);
      assertEquals("method_" + i, changeId);
      assertEquals("testuser", author);
      assertEquals((ignoredChangeIds.contains(changeId) ? ChangeState.IGNORED : ChangeState.EXECUTED).name(), state);
      assertNotNull(timestamp);
      assertEquals(changeLogClass.getName(), changeLogClassInstance);
      assertEquals("method_" + i, changeSetMethod);
      assertNotNull(executionMillis);
      checkMetadata(metadataResult);

    }
  }

  @NotNull
  private Map<String, Object> getStringObjectMap() {
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("string_key", "string_value");
    metadata.put("integer_key", 10);
    metadata.put("float_key", 11.11F);
    metadata.put("double_key", 12.12D);
    metadata.put("long_key", 13L);
    metadata.put("boolean_key", true);
    return metadata;
  }

  @Test
  public void shouldFail_WhenRunningChangeLog_IfChangeSetIdDuplicated() {
    collection = this.getDataBase().getCollection(CHANGELOG_COLLECTION_NAME);
    TestChangockRunner runner = TestChangockRunner.builder()
        .setDriver(getDriver())
        .addChangeLogsScanPackage(ChangeLogFailure.class.getPackage().getName())
        .build();
    exceptionRule.expect(ChangockException.class);
    exceptionRule.expectMessage("Duplicated changeset id found: 'id_duplicated'");
    runner.execute();
  }

  @Test
  public void shouldPassMongoDatabaseDecoratorToChangeSet() {
    CallVerifierImpl callVerifier = new CallVerifierImpl();
    collection = this.getDataBase().getCollection(CHANGELOG_COLLECTION_NAME);
    TestChangockRunner.builder()
        .setDriver(getDriver())
        .addChangeLogsScanPackage(ChangeLogEnsureDecorator.class.getPackage().getName())
        .addDependency(CallVerifier.class, callVerifier)
        .build()
        .execute();
    assertEquals(1, callVerifier.getCounter());
  }

  @Test
  public void shouldPrioritizeConnectorOverStandardDependencies_WhenThereIsConflict() {
    CallVerifierImpl callVerifier = new CallVerifierImpl();
    collection = this.getDataBase().getCollection(CHANGELOG_COLLECTION_NAME);
    TestChangockRunner.builder()
        .setDriver(getDriver())
        .addChangeLogsScanPackage(ChangeLogEnsureDecorator.class.getPackage().getName())
        .addDependency(CallVerifier.class, callVerifier)
        .addDependency(MongoDatabase.class, mock(MongoDatabase.class))// shouldn't use this, the one from the connector instead
        .build()
        .execute();
    assertEquals(1, callVerifier.getCounter());
  }

  private void checkMetadata(Map metadataResult) {
    assertEquals("string_value", metadataResult.get("string_key"));
    assertEquals(10, metadataResult.get("integer_key"));
    assertEquals(11.11F, (Double) metadataResult.get("float_key"), 0.01);
    assertEquals(12.12D, (Double) metadataResult.get("double_key"), 0.01);
    assertEquals(13L, metadataResult.get("long_key"));
    assertEquals(true, metadataResult.get("boolean_key"));
  }

  @NotNull
  private MongoSync4Driver getDriver() {
    MongoSync4Driver driver = new MongoSync4Driver(this.getDataBase());
    driver.setChangeLogCollectionName(CHANGELOG_COLLECTION_NAME);
    return driver;
  }
}
