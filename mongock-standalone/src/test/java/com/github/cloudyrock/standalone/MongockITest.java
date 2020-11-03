package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import com.github.cloudyrock.standalone.test.changelogs.MongockTestResource;
import com.github.cloudyrock.standalone.test.changelogs.allsuccess.AllSuccess1;
import com.github.cloudyrock.standalone.test.changelogs.runAlways.RunAlwaysChangeLog;
import com.github.cloudyrock.standalone.test.changelogs.withChangockAnnotations.ChangeLogwithChangockAnnotations;
import com.github.cloudyrock.standalone.utils.IndependentDbIntegrationTestBase;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class MongockITest extends IndependentDbIntegrationTestBase {

  private static final String CHANGELOG_COLLECTION_NAME = "mongockChangeLog";

  @Before
  public void init() {
  }

  @Test
  public void shouldExecuteAllChangeSets() {
    // given
    MongockStandalone.Runner runner = MongockStandalone.builder()
        .setDriver(getDriver())
        .addChangeLogsScanPackage(AllSuccess1.class.getPackage().getName())
        .buildRunner();


    // when
    runner.execute();

    // then


    MongoCursor<Document> iterator = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).find().iterator();
    List<Document> documents = new ArrayList<>();
    while(iterator.hasNext()) {
      documents.add(iterator.next());
    }

    // dbchangelog collection checking

    assertEquals(5, documents.size());
    assertEquals("AllSuccess1_method1", documents.get(0).getString("changeId"));
    assertEquals("AllSuccess2_method1", documents.get(1).getString("changeId"));
    assertEquals("InsideAllSuccess1_method1", documents.get(2).getString("changeId"));
    assertEquals("AllSuccess3_method1", documents.get(3).getString("changeId"));
    assertEquals("AllSuccess4_method1", documents.get(4).getString("changeId"));
  }

  @Test
  public void shouldRunTwice_WhenRunAlways() {
    // given
    MongockStandalone.Runner runner = MongockStandalone.builder()
        .setDriver(getDriver())
        .addChangeLogsScanPackage(RunAlwaysChangeLog.class.getPackage().getName())
        .buildRunner();

    // when
    runner.execute();
    runner.execute();

    // then

    // dbchangelog collection checking
    long changeSetWithRunAlways = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).countDocuments(new Document()
        .append("changeId", "runAlways")
        .append("author", "testuser"));
    assertEquals(2, changeSetWithRunAlways);

    long changeSetWithNoRunAlwaysExecuted = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).countDocuments(new Document()
        .append("changeId", "noRunAlways")
        .append("author", "testuser"));
    assertEquals(1, changeSetWithNoRunAlwaysExecuted);

    changeSetWithNoRunAlwaysExecuted = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).countDocuments(new Document()
        .append("changeId", "noRunAlways")
        .append("author", "testuser"));
    assertEquals(1, changeSetWithNoRunAlwaysExecuted);
  }


  @Test
  public void shouldStoreMetadata_WhenChangeSetIsTrack_IfAddedInBuilder() {
    // given
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("string_key", "string_value");
    metadata.put("integer_key", 10);
    metadata.put("float_key", 11.11F);
    metadata.put("double_key", 12.12D);
    metadata.put("long_key", 13L);
    metadata.put("boolean_key", true);
    MongockStandalone.Runner runner = MongockStandalone.builder()
        .setDriver(getDriver())
        .addChangeLogsScanPackage(MongockTestResource.class.getPackage().getName())
        .withMetadata(metadata)
        .buildRunner();

    // when
    runner.execute();

    // then
    Map metadataResult = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).find().first().get("metadata", Map.class);
    assertEquals("string_value", metadataResult.get("string_key"));
    assertEquals(10, metadataResult.get("integer_key"));
    assertEquals(11.11F, (Double) metadataResult.get("float_key"), 0.01);
    assertEquals(12.12D, (Double) metadataResult.get("double_key"), 0.01);
    assertEquals(13L, metadataResult.get("long_key"));
    assertEquals(true, metadataResult.get("boolean_key"));

  }

  @Test
  public void shouldExecuteChangockAnnotations() {
    // given
    MongockStandalone.Runner runner = MongockStandalone.builder()
        .setDriver(getDriver())
        .addChangeLogsScanPackage(ChangeLogwithChangockAnnotations.class.getPackage().getName())
        .buildRunner();


    // when
    runner.execute();

    // then
    final long changeWithChangockAnnotations = mongoDatabase.getCollection(CHANGELOG_COLLECTION_NAME).countDocuments(new Document()
        .append("changeId", "withChangockAnnotations")
        .append("author", "testuser")
        .append("state", "EXECUTED"));
    assertEquals(1, changeWithChangockAnnotations);
  }

  private MongoSync4Driver getDriver() {
    MongoSync4Driver driver = MongoSync4Driver.withDefaultLock(this.getMongoClient(), DEFAULT_DATABASE_NAME);
    driver.disableTransaction();
    driver.setChangeLogCollectionName(CHANGELOG_COLLECTION_NAME);
    return driver;
  }

}
