package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import com.github.cloudyrock.mongock.test.changelogs.MongockTestResource;
import com.github.cloudyrock.mongock.test.changelogs.runAlways.RunAlwaysChangeLog;
import com.github.cloudyrock.mongock.utils.IndependentDbIntegrationTestBase;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
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
    MongockStandalone runner = new MongockStandaloneBuilder(MongockTestResource.class.getPackage().getName())
        .setDriver(getDriver())
        .setLockQuickConfig()
        .build();


    // when
    runner.execute();
    runner.execute();

    // then

    // dbchangelog collection checking
    final long change1 = db.getCollection(CHANGELOG_COLLECTION_NAME).countDocuments(new Document()
        .append("changeId", "test1")
        .append("author", "testuser")
        .append("state", "EXECUTED"));
    assertEquals(1, change1);
  }

  @Test
  public void shouldRunTwice_WhenRunAlways() {
    // given
    MongockStandalone runner = new MongockStandaloneBuilder(RunAlwaysChangeLog.class.getPackage().getName())
        .setDriver(getDriver())
        .setLockQuickConfig()
        .build();


    // when
    runner.execute();
    runner.execute();

    // then

    // dbchangelog collection checking
    final long changeSetWithRunAlways = db.getCollection(CHANGELOG_COLLECTION_NAME).countDocuments(new Document()
        .append("changeId", "runAlways")
        .append("author", "testuser"));
    assertEquals(2, changeSetWithRunAlways);

    final long changeSetWithNoRunAlwaysExecuted = db.getCollection(CHANGELOG_COLLECTION_NAME).countDocuments(new Document()
        .append("changeId", "noRunAlways")
        .append("author", "testuser")
        .append("state", "EXECUTED"));
    assertEquals(1, changeSetWithNoRunAlwaysExecuted);

    final long changeSetWithNoRunAlwaysIgnored = db.getCollection(CHANGELOG_COLLECTION_NAME).countDocuments(new Document()
        .append("changeId", "noRunAlways")
        .append("author", "testuser")
        .append("state", "IGNORED"));
    assertEquals(1, changeSetWithNoRunAlwaysIgnored);
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
    MongockStandalone runner = new MongockStandaloneBuilder(MongockTestResource.class.getPackage().getName())
        .setDriver(getDriver())
        .setLockQuickConfig()
        .withMetadata(metadata)
        .build();

    // when
    runner.execute();

    // then
    Map metadataResult = db.getCollection(CHANGELOG_COLLECTION_NAME).find().first().get("metadata", Map.class);
    assertEquals("string_value", metadataResult.get("string_key"));
    assertEquals(10, metadataResult.get("integer_key"));
    assertEquals(11.11F, (Double) metadataResult.get("float_key"), 0.01);
    assertEquals(12.12D, (Double) metadataResult.get("double_key"), 0.01);
    assertEquals(13L, metadataResult.get("long_key"));
    assertEquals(true, metadataResult.get("boolean_key"));

  }

  private MongoSync4Driver getDriver() {
    MongoSync4Driver driver = new MongoSync4Driver(db);
    driver.setChangeLogCollectionName(CHANGELOG_COLLECTION_NAME);
    return driver;
  }

}
