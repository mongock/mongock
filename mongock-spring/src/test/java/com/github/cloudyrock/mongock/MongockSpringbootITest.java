package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.test.changelogs.MongockTestResource;
import com.github.cloudyrock.mongock.utils.IndependentDbIntegrationTestBase;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class MongockSpringbootITest extends IndependentDbIntegrationTestBase {

  private static final String CHANGELOG_COLLECTION_NAME = "mongockChangeLog";


  @Test
  public void shouldExecuteAllChangeSets() {
    // given
    SpringBootMongock runner = new SpringBootMongockBuilder(this.mongoClient, DEFAULT_DATABASE_NAME, MongockTestResource.class.getPackage().getName())
        .setLockQuickConfig()
        .setApplicationContext(Mockito.mock(ApplicationContext.class))
        .build();


    // when
    runner.execute();
    runner.execute();

    // then

    // dbchangelog collection checking
    long change1 = this.mongoClient.getDatabase(DEFAULT_DATABASE_NAME).getCollection(CHANGELOG_COLLECTION_NAME).count(new Document()
        .append(ChangeEntry.KEY_CHANGE_ID, "test1")
        .append(ChangeEntry.KEY_AUTHOR, "testuser"));
    assertEquals(1, change1);
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

    SpringBootMongock runner = new SpringBootMongockBuilder(this.mongoClient, DEFAULT_DATABASE_NAME, MongockTestResource.class.getPackage().getName())
        .setLockQuickConfig()
        .withMetadata(metadata)
        .setApplicationContext(Mockito.mock(ApplicationContext.class))
        .build();

    // when
    runner.execute();

    // then
    Map metadataResult = this.mongoClient.getDatabase(DEFAULT_DATABASE_NAME).getCollection(CHANGELOG_COLLECTION_NAME).find().first().get("metadata", Map.class);
    assertEquals("string_value", metadataResult.get("string_key"));
    assertEquals(10, metadataResult.get("integer_key"));
    assertEquals(11.11F, (Double) metadataResult.get("float_key"), 0.01);
    assertEquals(12.12D, (Double) metadataResult.get("double_key"), 0.01);
    assertEquals(13L, metadataResult.get("long_key"));
    assertEquals(true, metadataResult.get("boolean_key"));

  }

}
