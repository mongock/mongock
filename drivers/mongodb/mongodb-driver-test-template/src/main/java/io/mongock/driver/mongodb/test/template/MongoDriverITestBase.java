package io.mongock.driver.mongodb.test.template;


import com.github.cloudyrock.mongock.ChangeSet;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.api.entry.ChangeState;
import io.mongock.driver.core.driver.ConnectionDriverBase;
import io.mongock.driver.mongodb.test.template.integration.test1.withnorunalways.ChangeLogSuccess;
import io.mongock.driver.mongodb.test.template.integration.test1.withrunalways.WithRunAlways;
import io.mongock.driver.mongodb.test.template.integration.test2.ChangeLogFailure;
import io.mongock.driver.mongodb.test.template.integration.test3.ChangeLogEnsureDecorator;
import io.mongock.driver.mongodb.test.template.util.CallVerifier;
import io.mongock.driver.mongodb.test.template.util.CallVerifierImpl;
import io.mongock.driver.mongodb.test.template.util.IntegrationTestBase;
import io.mongock.driver.mongodb.test.template.util.MongoDBDriverTestAdapter;
import io.mongock.api.exception.MongockException;
import io.mongock.runner.core.executor.MongockRunner;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.mongock.runner.test.TestMongock;
import org.bson.Document;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
  public static final String ANY_EXECUTION_ID = "any";
  public static final String TEST_USER = "testuser";
  public static final String METHOD_PREFIX = "method_";

  @Test
  public void shouldRunAllChangeLogsSuccessfully() {
    runChangesAndCheck(getDriverWithTransactionDisabled(), ChangeLogSuccess.class, CHANGELOG_COLLECTION_NAME, Collections.emptyList(), false);
  }

  @Test
  public void shouldRegisterChangeSetAsIgnored_WhenAlreadyExecuted_IfNotRunAlways() throws NoSuchMethodException {
    MongoDBDriverTestAdapter adapter = getDefaultAdapter();
    adapter.insertOne(getChangeEntryDocument(ChangeLogSuccess.class.getMethod("method_0"), ChangeState.EXECUTED));
    runChangesAndCheck( getDriverWithTransactionDisabled(),ChangeLogSuccess.class, CHANGELOG_COLLECTION_NAME, Collections.singletonList("method_0"), false);
  }

  @Test
  public void shouldRegisterChangeSetAsExecuted_WhenAlreadyExecuted_IfRunAlways() throws NoSuchMethodException {
    MongoDBDriverTestAdapter adapter = getDefaultAdapter();
    Document changeEntry = getChangeEntryDocument(WithRunAlways.class.getMethod("method_0"), ChangeState.EXECUTED);
    adapter.insertOne(changeEntry);
    String executionId = UUID.randomUUID().toString();
    runChanges(getDriverWithTransactionDisabled(), WithRunAlways.class, executionId);
    checkChanges(WithRunAlways.class, CHANGELOG_COLLECTION_NAME, Collections.singletonList(changeEntry.getString(KEY_CHANGE_ID)), false, executionId);
    MongoCollection<Document> collection = this.getDataBase().getCollection(CHANGELOG_COLLECTION_NAME);
    assertNotNull(collection.find(new Document()
        .append(KEY_EXECUTION_ID, ANY_EXECUTION_ID)
        .append(KEY_AUTHOR, TEST_USER)
        .append(KEY_CHANGE_ID, METHOD_PREFIX + "0")).first());
  }

  @Test
  public void shouldRegisterChangeSetAsExecuted_WhenAlreadyIgnored_IfNotRunAlways() throws NoSuchMethodException {
    MongoDBDriverTestAdapter adapter = getDefaultAdapter();
    adapter.insertOne(getChangeEntryDocument(ChangeLogSuccess.class.getMethod("method_0"), ChangeState.IGNORED));
    runChangesAndCheck(getDriverWithTransactionDisabled(), ChangeLogSuccess.class, CHANGELOG_COLLECTION_NAME);
  }

  @Test
  public void shouldRegisterChangeSetAsExecuted_WhenAlreadyIgnored_IfRunAlways() throws NoSuchMethodException {
    MongoDBDriverTestAdapter adapter = getDefaultAdapter();
    adapter.insertOne(getChangeEntryDocument(WithRunAlways.class.getMethod("method_0"), ChangeState.IGNORED));
    runChangesAndCheck(getDriverWithTransactionDisabled(), WithRunAlways.class, CHANGELOG_COLLECTION_NAME);
  }

  @Test
  public void shouldRegisterChangeSetAsExecuted_WhenAlreadyFailed_IfNotRunAlways() throws NoSuchMethodException {
    MongoDBDriverTestAdapter adapter = getDefaultAdapter();
    adapter.insertOne(getChangeEntryDocument(ChangeLogSuccess.class.getMethod("method_0"), ChangeState.FAILED));
    runChangesAndCheck(getDriverWithTransactionDisabled(), ChangeLogSuccess.class, CHANGELOG_COLLECTION_NAME);
  }

  @Test
  public void shouldRegisterChangeSetAsExecuted_WhenAlreadyFailed_IfRunAlways() throws NoSuchMethodException {
    MongoDBDriverTestAdapter adapter = getDefaultAdapter();
    adapter.insertOne(getChangeEntryDocument(WithRunAlways.class.getMethod("method_0"), ChangeState.FAILED));
    runChangesAndCheck(getDriverWithTransactionDisabled(), WithRunAlways.class, CHANGELOG_COLLECTION_NAME);
  }

  @Test
  public void shouldUseDifferentChangeLogCollectionName_whenSettingChangeLogCollectionName() {
    String newChangeLogCollectionName = "newChangeLogCollectionName";
    ConnectionDriverBase driver = getDriverWithTransactionDisabled();
    driver.setChangeLogRepositoryName(newChangeLogCollectionName);
    runChangesAndCheck(driver, ChangeLogSuccess.class, newChangeLogCollectionName, Collections.emptyList(), false);
  }

  private Document getChangeEntryDocument(Method method, ChangeState state) {
    ChangeSet changeSet = method.getAnnotation(ChangeSet.class);
    return new Document()
        .append(KEY_EXECUTION_ID, ANY_EXECUTION_ID)
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
    MongockException ex = assertThrows(MongockException.class, () ->
            TestMongock.builder()
              .setDriver(getDriverWithTransactionDisabled())
              .addChangeLogsScanPackage(ChangeLogFailure.class.getPackage().getName())
              .buildRunner()
    );
    assertEquals("Duplicated changeset id found: 'id_duplicated'", ex.getMessage());
  }

  @Test
  public void shouldPassMongoDatabaseDecoratorToChangeSet() {

    CallVerifierImpl callVerifier = new CallVerifierImpl();
    TestMongock.builder()
        .setDriver(getDriverWithTransactionDisabled())
        .addChangeLogsScanPackage(ChangeLogEnsureDecorator.class.getPackage().getName())
        .addDependency(CallVerifier.class, callVerifier)
        .buildRunner()
        .execute();
    assertEquals(1, callVerifier.getCounter());
  }

  @Test
  public void shouldPrioritizeConnectorOverStandardDependencies_WhenThereIsConflict() {
    CallVerifierImpl callVerifier = new CallVerifierImpl();
    TestMongock.builder()
        .setDriver(getDriverWithTransactionDisabled())
        .addChangeLogsScanPackage(ChangeLogEnsureDecorator.class.getPackage().getName())
        .addDependency(CallVerifier.class, callVerifier)
        .addDependency(MongoDatabase.class, mock(MongoDatabase.class))// shouldn't use this, the one from the connector instead
        .buildRunner()
        .execute();
    assertEquals(1, callVerifier.getCounter());
  }

  @Test
  public void shouldThrowException_WhenNotIndexCreation_IfNotCreatedBefore() {
    // given
    ConnectionDriverBase driver = getDriverWithTransactionDisabled();
    driver.setIndexCreation(false);

    //when
    MongockException ex = assertThrows(MongockException.class, () ->
            TestMongock.builder()
              .setDriver(driver)
              .addChangeLogsScanPackage(ChangeLogSuccess.class.getPackage().getName())
              .buildRunner()
              .execute()
    );
    assertEquals("Index creation not allowed, but not created or wrongly created", ex.getMessage());
  }

  @Test
  public void shouldBeOk_WhenNotIndexCreation_IfCreatedBefore() {
    // given
    ConnectionDriverBase driver = getDriverWithTransactionDisabled();
    driver.setIndexCreation(false);
    getAdapter(CHANGELOG_COLLECTION_NAME).createUniqueIndex("executionId", "author", "changeId");
    driver.setLockRepositoryName(LOCK_COLLECTION_NAME);
    getAdapter(LOCK_COLLECTION_NAME).createUniqueIndex("key");

    //when
    TestMongock.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(ChangeLogSuccess.class.getPackage().getName())
        .buildRunner()
        .execute();
  }


  @Test
  public void shouldThrowException_WhenNotIndexCreation_IfWrongLockIndexCreated() {
    // given
    ConnectionDriverBase driver = getDriverWithTransactionDisabled();
    driver.setIndexCreation(false);
    getAdapter(CHANGELOG_COLLECTION_NAME).createUniqueIndex("executionId", "author", "changeId");
    driver.setLockRepositoryName(LOCK_COLLECTION_NAME);
    getAdapter(LOCK_COLLECTION_NAME).createUniqueIndex("keywrong");

    //when
    MongockException ex = assertThrows(MongockException.class, () ->
            TestMongock.builder()
              .setDriver(driver)
              .addChangeLogsScanPackage(ChangeLogSuccess.class.getPackage().getName())
              .buildRunner()
              .execute()
    );
    assertEquals("Index creation not allowed, but not created or wrongly created", ex.getMessage());
  }

  @Test
  public void shouldThrowException_WhenNotIndexCreation_IfChangeLogIndexPartiallyCreated() {
    // given
    ConnectionDriverBase driver = getDriverWithTransactionDisabled();
    driver.setIndexCreation(false);
    getAdapter(CHANGELOG_COLLECTION_NAME).createUniqueIndex("executionId_wrong", "author", "changeId");
    driver.setLockRepositoryName(LOCK_COLLECTION_NAME);
    getAdapter(LOCK_COLLECTION_NAME).createUniqueIndex("key");

    //when
    MongockException ex = assertThrows(MongockException.class, () ->
            TestMongock.builder()
              .setDriver(driver)
              .addChangeLogsScanPackage(ChangeLogSuccess.class.getPackage().getName())
              .buildRunner()
              .execute()
    );
    assertEquals("Index creation not allowed, but not created or wrongly created", ex.getMessage());
  }




  private void runChangesAndCheck(ConnectionDriver driver, Class changeLogClass, String changeLogCollectionName) {
    runChangesAndCheck(driver, changeLogClass, changeLogCollectionName, Collections.emptyList(), false);
  }



  private void runChangesAndCheck(ConnectionDriver driver, Class changeLogClass, String chageLogCollectionName, Collection<String> ignoredChangeIds, boolean trackIgnored) {
    String executionId = UUID.randomUUID().toString();

    runChanges(driver, changeLogClass, executionId);

    checkChanges(changeLogClass, chageLogCollectionName, ignoredChangeIds, trackIgnored, executionId);
  }

  private void checkChanges(Class changeLogClass, String chageLogCollectionName, Collection<String> ignoredOrRunAlwaysChangeIds, boolean trackIgnored, String executionId) {
    MongoCollection<Document> collection = this.getDataBase().getCollection(chageLogCollectionName);
    for (int i = 0; i < 5; i++) {
      Document change = collection.find(new Document()
          .append(KEY_EXECUTION_ID, executionId)
          .append(KEY_AUTHOR, TEST_USER)
          .append(KEY_CHANGE_ID, "method_" + i)).first();
      if(trackIgnored || !ignoredOrRunAlwaysChangeIds.contains("method_" + i)) {

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
        assertEquals(TEST_USER, author);
        assertEquals((ignoredOrRunAlwaysChangeIds.contains(changeId) ? ChangeState.IGNORED : ChangeState.EXECUTED).name(), state);
        assertNotNull(timestamp);
        assertEquals(changeLogClass.getName(), changeLogClassInstance);
        assertEquals("method_" + i, changeSetMethod);
        assertNotNull(executionMillis);
        checkMetadata(metadataResult);
      }

    }
  }

  private void runChanges(ConnectionDriver driver, Class changeLogClass, String executionId) {
    MongockRunner runner = TestMongock.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(changeLogClass.getPackage().getName())
        .withMetadata(getStringObjectMap())
        .setExecutionId(executionId)
        .buildRunner();
    runner.execute();
  }


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

  protected abstract ConnectionDriverBase getDriverWithTransactionDisabled();


}
