package com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.driver;


import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.SpringDataMongo2Driver;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.integration.test1.ChangeLogSuccess;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.integration.test2.ChangeLogFailure;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.integration.test3.ChangeLogEnsureDecorator;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.integration.test4.ChangeLogWithMongoTemplate;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.util.CallVerifier;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.util.CallVerifierImpl;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v2.util.IntegrationTestBase;
import io.changock.migration.api.exception.ChangockException;
import io.changock.runner.standalone.TestChangockRunner;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class MongoDriverITest extends IntegrationTestBase {

  private static final String CHANGELOG_COLLECTION_NAME = "changockChangeLog";


  private static final String KEY_EXECUTION_ID = "executionId";
  private static final String KEY_CHANGE_ID = "changeId";
  private static final String KEY_AUTHOR = "author";
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
    runChanges(new SpringDataMongo2Driver(getMongoTemplate()), CHANGELOG_COLLECTION_NAME);
  }

  @NotNull
  private MongoTemplate getMongoTemplate() {
    return new MongoTemplate(this.getMongoClient(), this.getDataBase().getName());
  }

  @Test
  public void shouldUseDifferentChangeLogCollectionName_whenSettingChangeLogCollectionName() {
    String newChangeLogCollectionName = "newChangeLogCollectionName";
    collection = this.getDataBase().getCollection(newChangeLogCollectionName);
    SpringDataMongo2Driver driver = new SpringDataMongo2Driver(this.getMongoTemplate());
    driver.setChangeLogCollectionName(newChangeLogCollectionName);
    runChanges(driver, newChangeLogCollectionName);
  }


  private void runChanges(SpringDataMongo2Driver driver, String chageLogCollectionName) {
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("string_key", "string_value");
    metadata.put("integer_key", 10);
    metadata.put("float_key", 11.11F);
    metadata.put("double_key", 12.12D);
    metadata.put("long_key", 13L);
    metadata.put("boolean_key", true);

    TestChangockRunner runner = TestChangockRunner.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(ChangeLogSuccess.class.getPackage().getName())
        .withMetadata(metadata)
        .build();

    runner.execute();

    collection = this.getDataBase().getCollection(chageLogCollectionName);

    for (int i = 0; i < 5; i++) {
      Document change = collection.find(new Document()
          .append(KEY_AUTHOR, "testuser")
          .append(KEY_CHANGE_ID, "ChangeLog1_" + i)).first();
      String executionId = change.get(KEY_EXECUTION_ID, String.class);
      String changeId = change.get(KEY_CHANGE_ID, String.class);
      String author = change.get(KEY_AUTHOR, String.class);
      Date timestamp = change.get(KEY_TIMESTAMP, Date.class);
      String changeLogClass = change.get(KEY_CHANGE_LOG_CLASS, String.class);
      String changeSetMethod = change.get(KEY_CHANGE_SET_METHOD, String.class);
      Long executionMillis = change.get(KEY_EXECUTION_MILLIS, Long.class);
      Map metadataResult = change.get(KEY_METADATA, Map.class);
      assertNotNull(executionId);
      assertEquals("ChangeLog1_" + i, changeId);
      assertEquals("testuser", author);
      assertNotNull(timestamp);
      assertEquals(ChangeLogSuccess.class.getName(), changeLogClass);
      assertEquals("method_" + i, changeSetMethod);
      assertNotNull(executionMillis);
      checkMetadata(metadataResult);

    }
  }

  @Test
  public void shouldFail_WhenRunningChangeLog_IfChangeSetIdDuplicated() {
    collection = this.getDataBase().getCollection(CHANGELOG_COLLECTION_NAME);
    TestChangockRunner runner = TestChangockRunner.builder()
        .setDriver(new SpringDataMongo2Driver(this.getMongoTemplate()))
        .addChangeLogsScanPackage(ChangeLogFailure.class.getPackage().getName())
        .build();
    exceptionRule.expect(ChangockException.class);
    exceptionRule.expectMessage("Duplicated changeset id found: 'id_duplicated'");
    runner.execute();
  }

  @Test
  public void shouldPassDecoratorsToChangeSet() {
    CallVerifierImpl callVerifier = new CallVerifierImpl();
    collection = this.getDataBase().getCollection(CHANGELOG_COLLECTION_NAME);
    TestChangockRunner runner = TestChangockRunner.builder()
        .setDriver(new SpringDataMongo2Driver(this.getMongoTemplate()))
        .addChangeLogsScanPackage(ChangeLogEnsureDecorator.class.getPackage().getName())
        .addDependency(CallVerifier.class, callVerifier)
        .build();

    runner.execute();
    assertEquals(2, callVerifier.getCounter());
  }

  @Test
  public void shouldPrioritizeConnectorOverStandardDependencies_WhenThereIsConflict() {
    CallVerifierImpl callVerifier = new CallVerifierImpl();
    collection = this.getDataBase().getCollection(CHANGELOG_COLLECTION_NAME);
    TestChangockRunner runner = TestChangockRunner.builder()
        .setDriver(new SpringDataMongo2Driver(this.getMongoTemplate()))
        .addChangeLogsScanPackage(ChangeLogEnsureDecorator.class.getPackage().getName())
        .addDependency(CallVerifier.class, callVerifier)
        .build();

    runner.execute();
    assertEquals(2, callVerifier.getCounter());
  }

  @Test
  public void shouldFail_whenRunningChangeSet_ifMongoTemplateParameter() {
    collection = this.getDataBase().getCollection(CHANGELOG_COLLECTION_NAME);
    TestChangockRunner runner = TestChangockRunner.builder()
        .setDriver(new SpringDataMongo2Driver(this.getMongoTemplate()))
        .addChangeLogsScanPackage(ChangeLogWithMongoTemplate.class.getPackage().getName())
        .addDependency(MongoTemplate.class, mock(MongoTemplate.class))// shouldn't use this, the one from the connector instead
        .build();

    exceptionRule.expect(ChangockException.class);
    exceptionRule.expectMessage("Error in method[ChangeLogWithMongoTemplate.shouldFailBecauseMongoTemplate] : Forbidden parameter[MongoTemplate]. Must be replaced with [MongockTemplate]");
    runner.execute();
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
