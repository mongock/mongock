package com.github.cloudyrock.mongock.driver.mongodb.test.template;


import com.github.cloudyrock.mongock.MongockConnectionDriver;
import com.github.cloudyrock.mongock.driver.mongodb.test.template.integration.test1.withnorunalways.ChangeLogSuccess;
import com.github.cloudyrock.mongock.driver.mongodb.test.template.integration.test1.withrunalways.WithRunAlways;
import com.github.cloudyrock.mongock.driver.mongodb.test.template.integration.test2.ChangeLogFailure;
import com.github.cloudyrock.mongock.driver.mongodb.test.template.integration.test3.ChangeLogEnsureDecorator;
import com.github.cloudyrock.mongock.driver.mongodb.test.template.util.CallVerifier;
import com.github.cloudyrock.mongock.driver.mongodb.test.template.util.CallVerifierImpl;
import com.github.cloudyrock.mongock.driver.mongodb.test.template.util.IntegrationTestBase;
import com.github.cloudyrock.mongock.driver.mongodb.test.template.util.MongoDBDriverTestAdapter;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public abstract class MongoDriverITestBase extends IntegrationTestBase {

  protected static final String KEY_EXECUTION_ID = "executionId";
  protected static final String KEY_CHANGE_ID = "changeId";
  protected static final String KEY_AUTHOR = "author";
  protected static final String KEY_STATE = "state";
  protected static final String KEY_TIMESTAMP = "timestamp";
  protected static final String KEY_CHANGE_LOG_CLASS = "changeLogClass";
  protected static final String KEY_CHANGE_SET_METHOD = "changeSetMethod";
  protected static final String KEY_EXECUTION_MILLIS = "executionMillis";
  protected static final String KEY_METADATA = "metadata";
//  private final MongoDbDriverTestAdapter adapter;

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void shouldRunAllChangeLogsSuccessfully() {
    runChanges(getDriverWithTransactionDisabled(), ChangeLogSuccess.class, CHANGELOG_COLLECTION_NAME, Collections.emptyList(), false);
  }

  @Test
  public void shouldRegisterChangeSetAsIgnored_WhenAlreadyExecuted_IfNotRunAlways() throws NoSuchMethodException {
    MongoDBDriverTestAdapter adapter = getDefaultAdapter();
    adapter.insertOne(getChangeEntryDocument(ChangeLogSuccess.class.getMethod("method_0"), ChangeState.EXECUTED));
    runChanges( getDriverWithTransactionDisabled(),ChangeLogSuccess.class, CHANGELOG_COLLECTION_NAME, Collections.singletonList("method_0"), false);
  }

  @Test
  public void shouldRegisterChangeSetAsExecuted_WhenAlreadyExecuted_IfRunAlways() throws NoSuchMethodException {
    MongoDBDriverTestAdapter adapter = getDefaultAdapter();
    adapter.insertOne(getChangeEntryDocument(WithRunAlways.class.getMethod("method_0"), ChangeState.EXECUTED));
    runChanges(getDriverWithTransactionDisabled(), WithRunAlways.class, CHANGELOG_COLLECTION_NAME);
  }

  @Test
  public void shouldRegisterChangeSetAsExecuted_WhenAlreadyIgnored_IfNotRunAlways() throws NoSuchMethodException {
    MongoDBDriverTestAdapter adapter = getDefaultAdapter();
    adapter.insertOne(getChangeEntryDocument(ChangeLogSuccess.class.getMethod("method_0"), ChangeState.IGNORED));
    runChanges(getDriverWithTransactionDisabled(), ChangeLogSuccess.class, CHANGELOG_COLLECTION_NAME);
  }

  @Test
  public void shouldRegisterChangeSetAsExecuted_WhenAlreadyIgnored_IfRunAlways() throws NoSuchMethodException {
    MongoDBDriverTestAdapter adapter = getDefaultAdapter();
    adapter.insertOne(getChangeEntryDocument(WithRunAlways.class.getMethod("method_0"), ChangeState.IGNORED));
    runChanges(getDriverWithTransactionDisabled(), WithRunAlways.class, CHANGELOG_COLLECTION_NAME);
  }

  @Test
  public void shouldRegisterChangeSetAsExecuted_WhenAlreadyFailed_IfNotRunAlways() throws NoSuchMethodException {
    MongoDBDriverTestAdapter adapter = getDefaultAdapter();
    adapter.insertOne(getChangeEntryDocument(ChangeLogSuccess.class.getMethod("method_0"), ChangeState.FAILED));
    runChanges(getDriverWithTransactionDisabled(), ChangeLogSuccess.class, CHANGELOG_COLLECTION_NAME);
  }

  @Test
  public void shouldRegisterChangeSetAsExecuted_WhenAlreadyFailed_IfRunAlways() throws NoSuchMethodException {
    MongoDBDriverTestAdapter adapter = getDefaultAdapter();
    adapter.insertOne(getChangeEntryDocument(WithRunAlways.class.getMethod("method_0"), ChangeState.FAILED));
    runChanges(getDriverWithTransactionDisabled(), WithRunAlways.class, CHANGELOG_COLLECTION_NAME);
  }

  @Test
  public void shouldUseDifferentChangeLogCollectionName_whenSettingChangeLogCollectionName() {
    String newChangeLogCollectionName = "newChangeLogCollectionName";
    MongockConnectionDriver driver = getDriverWithTransactionDisabled();
    driver.setChangeLogCollectionName(newChangeLogCollectionName);
    runChanges(driver, ChangeLogSuccess.class, newChangeLogCollectionName, Collections.emptyList(), false);
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


  @Test
  public void shouldFail_WhenRunningChangeLog_IfChangeSetIdDuplicated() {
    TestChangockRunner runner = TestChangockRunner.builder()
        .setDriver(getDriverWithTransactionDisabled())
        .addChangeLogsScanPackage(ChangeLogFailure.class.getPackage().getName())
        .build();
    exceptionRule.expect(ChangockException.class);
    exceptionRule.expectMessage("Duplicated changeset id found: 'id_duplicated'");
    runner.execute();
  }

  @Test
  public void shouldPassMongoDatabaseDecoratorToChangeSet() {
    CallVerifierImpl callVerifier = new CallVerifierImpl();
    TestChangockRunner.builder()
        .setDriver(getDriverWithTransactionDisabled())
        .addChangeLogsScanPackage(ChangeLogEnsureDecorator.class.getPackage().getName())
        .addDependency(CallVerifier.class, callVerifier)
        .build()
        .execute();
    assertEquals(1, callVerifier.getCounter());
  }

  @Test
  public void shouldPrioritizeConnectorOverStandardDependencies_WhenThereIsConflict() {
    CallVerifierImpl callVerifier = new CallVerifierImpl();
    TestChangockRunner.builder()
        .setDriver(getDriverWithTransactionDisabled())
        .addChangeLogsScanPackage(ChangeLogEnsureDecorator.class.getPackage().getName())
        .addDependency(CallVerifier.class, callVerifier)
        .addDependency(MongoDatabase.class, mock(MongoDatabase.class))// shouldn't use this, the one from the connector instead
        .build()
        .execute();
    assertEquals(1, callVerifier.getCounter());
  }

  @Test
  public void shouldThrowException_WhenNotIndexCreation_IfNotCreatedBefore() {
    // given
    MongockConnectionDriver driver = getDriverWithTransactionDisabled();
    driver.setIndexCreation(false);

    //then
    exceptionRule.expect(ChangockException.class);
    exceptionRule.expectMessage("Index creation not allowed, but not created or wrongly created");

    //when
    TestChangockRunner.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(ChangeLogSuccess.class.getPackage().getName())
        .build()
        .execute();
  }

  @Test
  public void shouldBeOk_WhenNotIndexCreation_IfCreatedBefore() {
    // given
    MongockConnectionDriver driver = getDriverWithTransactionDisabled();
    driver.setIndexCreation(false);
    getAdapter(CHANGELOG_COLLECTION_NAME).createUniqueIndex("executionId", "author", "changeId");
    driver.setLockCollectionName(LOCK_COLLECTION_NAME);
    getAdapter(LOCK_COLLECTION_NAME).createUniqueIndex("key");

    //when
    TestChangockRunner.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(ChangeLogSuccess.class.getPackage().getName())
        .build()
        .execute();
  }


  @Test
  public void shouldThrowException_WhenNotIndexCreation_IfWrongLockIndexCreated() {
    // given
    MongockConnectionDriver driver = getDriverWithTransactionDisabled();
    driver.setIndexCreation(false);
    getAdapter(CHANGELOG_COLLECTION_NAME).createUniqueIndex("executionId", "author", "changeId");
    driver.setLockCollectionName(LOCK_COLLECTION_NAME);
    getAdapter(LOCK_COLLECTION_NAME).createUniqueIndex("keywrong");

    //then
    exceptionRule.expect(ChangockException.class);
    exceptionRule.expectMessage("Index creation not allowed, but not created or wrongly created");

    //when
    TestChangockRunner.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(ChangeLogSuccess.class.getPackage().getName())
        .build()
        .execute();
  }

  @Test
  public void shouldThrowException_WhenNotIndexCreation_IfChangeLogIndexPartiallyCreated() {
    // given
    MongockConnectionDriver driver = getDriverWithTransactionDisabled();
    driver.setIndexCreation(false);
    getAdapter(CHANGELOG_COLLECTION_NAME).createUniqueIndex("executionId_wrong", "author", "changeId");
    driver.setLockCollectionName(LOCK_COLLECTION_NAME);
    getAdapter(LOCK_COLLECTION_NAME).createUniqueIndex("key");

    //then
    exceptionRule.expect(ChangockException.class);
    exceptionRule.expectMessage("Index creation not allowed, but not created or wrongly created");

    //when
    TestChangockRunner.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(ChangeLogSuccess.class.getPackage().getName())
        .build()
        .execute();
  }




  private void runChanges(MongockConnectionDriver driver, Class changeLogClass, String changeLogCollectionName) {
    runChanges(driver, changeLogClass, changeLogCollectionName, Collections.emptyList(), false);
  }



  private void runChanges(MongockConnectionDriver driver, Class changeLogClass, String chageLogCollectionName, Collection<String> ignoredChangeIds, boolean trackIgnored) {
    Map<String, Object> metadata = getStringObjectMap();

    String executionId = UUID.randomUUID().toString();
    TestChangockRunner runner = TestChangockRunner.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(changeLogClass.getPackage().getName())
        .withMetadata(metadata)
        .setExecutionId(executionId)
        .build();
    runner.execute();

    MongoCollection<Document> collection = this.getDataBase().getCollection(chageLogCollectionName);

    FindIterable<Document> documents = this.getDataBase().getCollection(chageLogCollectionName).find(new Document()
        .append(KEY_EXECUTION_ID, executionId));
    MongoCursor<Document> iterator = documents.iterator();
    while (iterator.hasNext()) {
      Document next = iterator.next();
      System.out.println(next);
    }

    for (int i = 0; i < 5; i++) {
      Document change = collection.find(new Document()
          .append(KEY_EXECUTION_ID, executionId)
          .append(KEY_AUTHOR, "testuser")
          .append(KEY_CHANGE_ID, "method_" + i)).first();
      if(trackIgnored || !ignoredChangeIds.contains("method_" + i)) {

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


  private void checkMetadata(Map metadataResult) {
    assertEquals("string_value", metadataResult.get("string_key"));
    assertEquals(10, metadataResult.get("integer_key"));
    assertEquals(11.11F, (Double) metadataResult.get("float_key"), 0.01);
    assertEquals(12.12D, (Double) metadataResult.get("double_key"), 0.01);
    assertEquals(13L, metadataResult.get("long_key"));
    assertEquals(true, metadataResult.get("boolean_key"));
  }

  protected abstract MongockConnectionDriver getDriverWithTransactionDisabled();


}
