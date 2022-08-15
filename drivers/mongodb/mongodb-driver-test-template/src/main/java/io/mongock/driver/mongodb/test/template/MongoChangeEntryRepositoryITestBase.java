package io.mongock.driver.mongodb.test.template;

import com.mongodb.client.model.IndexOptions;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.driver.api.entry.ChangeEntryService;
import io.mongock.driver.mongodb.test.template.util.IntegrationTestBase;
import org.bson.Document;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public abstract class MongoChangeEntryRepositoryITestBase extends IntegrationTestBase {

  protected ChangeEntryService repository;

  @Test
  public void shouldThrowException_WhenNoIndexCreation_IfIndexNoPreviouslyCreated() throws MongockException {
    MongockException ex = assertThrows(MongockException.class, () ->
            initializeRepository(false)
    );
    assertEquals("Index creation not allowed, but not created or wrongly created for collection", ex.getMessage());
  }


  @Test
  public void shouldBeOk_WhenNoIndexCreation_IfIndexAlreadyCreated() throws MongockException {
    getDefaultAdapter().createIndex(getIndexDocument(new String[]{"executionId", "author", "changeId"}), new IndexOptions().unique(true));
    initializeRepository(false);
  }

  @Test
  public void getEntriesLog_WorksRegardlessOfExecutionMillisMissingIntegerOrLong() throws MongockException {
    initializeRepository(true);
    createAndInsertChangeEntry(false, null, "changeId1", "author", "executionId1", Long.MAX_VALUE);
    createAndInsertChangeEntry(false, null, "changeId2", "author", "executionId2", Integer.MAX_VALUE);
    createAndInsertChangeEntry(false, null, "changeId3", "author", "executionId3", null);
    List<ChangeEntry> result = repository.getEntriesLog();
    assertEquals(3, result.size());
  }

  private void createAndInsertChangeEntry(boolean withState, String state, String changeId, String author, String executionId, Number executionMillis) {
    Document existingEntry = new Document()
        .append("executionId", executionId)
        .append("changeId", changeId)
        .append("author", author)
        .append("timestamp", Date.from(Instant.now()))
        .append("executionMillis", executionMillis)
        .append("changeLogClass", "anyClass")
        .append("changeSetMethod", "anyMethod")
        .append("metadata", null);
    if (withState) {
      existingEntry = existingEntry.append("state", state);
    }
    getDefaultAdapter().insertOne(existingEntry);
  }

  protected abstract void initializeRepository(boolean indexCreation);

  protected Document getIndexDocument(String[] uniqueFields) {
    final Document indexDocument = new Document();
    for (String field : uniqueFields) {
      indexDocument.append(field, 1);
    }
    return indexDocument;
  }
}
