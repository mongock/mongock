package com.github.cloudyrock.mongock.driver.mongodb.v3.repository;

import com.github.cloudyrock.mongock.driver.mongodb.v3.driver.util.IntegrationTestBase;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.entry.ChangeState;
import io.changock.migration.api.exception.ChangockException;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class MongoChangeEntryRepositoryITest extends IntegrationTestBase {

  private static final String CHANGE_ENTRY_COLLECTION_NAME = "dbchangelog";
  private Mongo3ChangeEntryRepository<ChangeEntry> repository;

  @Before
  public void setUp() {
    collection = getDataBase().getCollection(CHANGE_ENTRY_COLLECTION_NAME);
    repository = Mockito.spy(new Mongo3ChangeEntryRepository<>(collection));
    repository.initialize();
  }

  @Test
  public void shouldCreateUniqueIndex_whenEnsureIndex_IfNotCreatedYet() throws ChangockException {

    //then
    verify(repository, times(1)).createRequiredUniqueIndex();
    // and not
    verify(repository, times(0)).dropIndex(any(Document.class));
  }

  @Test
  public void shouldNoCreateUniqueIndex_whenEnsureIndex_IfAlreadyCreated() throws ChangockException {
    // given
    collection = getDataBase().getCollection(CHANGE_ENTRY_COLLECTION_NAME);
    repository = Mockito.spy(new Mongo3ChangeEntryRepository(collection));

    doReturn(true).when(repository).isUniqueIndex(any(Document.class));

    // when
    repository.initialize();

    //then
    verify(repository, times(0)).createRequiredUniqueIndex();
    // and not
    verify(repository, times(0)).dropIndex(new Document());
  }

  @Test
  public void shouldReturnFalse_whenHasNotBeenExecuted_IfThereIsWithSameIdAndAuthorAndStateNull() {
    String changeId = "changeId";
    String author = "author";
    String executionId = "executionId";
    createAndInsertChangeEntry(true, null, changeId, author, executionId);
    Assert.assertEquals("pre-requisite: changeEntry should be added", 1,
        collection.countDocuments(new Document().append("changeId", changeId).append("author", author)));

    Assert.assertTrue(repository.isAlreadyExecuted(changeId, author));
  }

  @Test
  public void shouldReturnFalse_whenHasNotBeenExecuted_IfThereIsWithSameIdAndAuthorAndNoState() {
    String changeId = "changeId";
    String author = "author";
    String executionId = "executionId";
    createAndInsertChangeEntry(false, null, changeId, author, executionId);
    Assert.assertEquals("pre-requisite: changeEntry should be added", 1,
        collection.countDocuments(new Document().append("changeId", changeId).append("author", author)));

    Assert.assertTrue(repository.isAlreadyExecuted(changeId, author));
  }


  @Test
  public void shouldReturnFalse_whenHasNotBeenExecuted_IfThereIsWithSameIdAndAuthorAndStateEXECUTED() {
    String changeId = "changeId";
    String author = "author";
    String executionId = "executionId";
    createAndInsertChangeEntry(true, ChangeState.EXECUTED.toString(), changeId, author, executionId);
    Assert.assertEquals("pre-requisite: changeEntry should be added", 1,
        collection.countDocuments(new Document().append("changeId", changeId).append("author", author)));

    Assert.assertTrue(repository.isAlreadyExecuted(changeId, author));
  }

  @Test
  public void shouldReturnTrue_whenHasNotBeenExecuted_IfThereIsWithSameIdAndAuthorAndStateIGNORED() {
    String changeId = "changeId";
    String author = "author";
    String executionId = "executionId";
    createAndInsertChangeEntry(true, ChangeState.IGNORED.toString(), changeId, author, executionId);
    Assert.assertEquals("pre-requisite: changeEntry should be added", 1,
        collection.countDocuments(new Document().append("changeId", changeId).append("author", author)));

    Assert.assertFalse(repository.isAlreadyExecuted(changeId, author));
  }

  @Test
  public void shouldReturnTrue_whenHasNotBeenExecuted_IfThereIsWithSameIdAndAuthorAndStateFAILED() {
    String changeId = "changeId";
    String author = "author";
    String executionId = "executionId";
    createAndInsertChangeEntry(true, ChangeState.FAILED.toString(), changeId, author, executionId);
    Assert.assertEquals("pre-requisite: changeEntry should be added", 1,
        collection.countDocuments(new Document().append("changeId", changeId).append("author", author)));

    Assert.assertFalse(repository.isAlreadyExecuted(changeId, author));
  }


  private void createAndInsertChangeEntry(boolean withState, String state, String changeId, String author, String executionId) {
    Document existingEntry = new Document()
        .append("executionId", executionId)
        .append("changeId", changeId)
        .append("author", author)
        .append("timestamp", Date.from(Instant.now()))
        .append("changeLogClass", "anyClass")
        .append("changeSetMethod", "anyMethod")
        .append("metadata", null);
    if (withState) {
      existingEntry = existingEntry.append("state", state);
    }
    collection.insertOne(existingEntry);
  }

}
