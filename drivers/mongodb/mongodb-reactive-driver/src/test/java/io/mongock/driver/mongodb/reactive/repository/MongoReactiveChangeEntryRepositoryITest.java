package io.mongock.driver.mongodb.reactive.repository;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.mongodb.reactive.MongoDbReactiveDriverTestAdapterImpl;
import io.mongock.driver.mongodb.reactive.util.IntegrationTestBase;
import io.mongock.driver.mongodb.reactive.util.MongoDBDriverTestAdapter;
import io.mongock.driver.mongodb.reactive.util.RepositoryAccessorHelper;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MongoReactiveChangeEntryRepositoryITest extends IntegrationTestBase {

  protected MongoReactiveChangeEntryRepository repository;


  protected void initializeRepository(boolean indexCreation) {
    MongoReactiveChangeEntryRepository repo = new MongoReactiveChangeEntryRepository(getDataBase().getCollection(CHANGELOG_COLLECTION_NAME));
    repo.setIndexCreation(indexCreation);
    repository = Mockito.spy(repo);
    repository.initialize();
  }




  @Test
  public void shouldPassedReadWriteConcerns_whenCreating_ifConfigurationIsPassed() {

    //given
    WriteConcern expectedWriteConcern = WriteConcern.W1;
    ReadConcern expectedReadConcern = ReadConcern.LINEARIZABLE;
    ReadPreference expectedReadPreference = ReadPreference.nearest();
    ReadWriteConfiguration readWriteConfiguration = new ReadWriteConfiguration(expectedWriteConcern, expectedReadConcern, expectedReadPreference);

    //then
    testReadWriteConcern(expectedWriteConcern, expectedReadConcern, expectedReadPreference, readWriteConfiguration);
  }

  //TODO THIS SHOULD BE MOVED TO MongoChangeEntryRepositoryITestBase FOR REUSE

  @Test
  public void shouldCreateUniqueIndex_whenEnsureIndex_IfNotCreatedYet() throws MongockException {
    initializeRepository(true);

    //then
    verify(repository, times(1)).createRequiredUniqueIndex();
    // and not
    verify(repository, times(0)).dropIndex(any(Document.class));
  }

  @Test
  public void shouldNoCreateUniqueIndex_whenEnsureIndex_IfAlreadyCreated() throws MongockException {
    initializeRepository(true);
    // given
    repository = Mockito.spy(new MongoReactiveChangeEntryRepository(getDataBase().getCollection(CHANGELOG_COLLECTION_NAME)));

    doReturn(true).when((MongoReactiveChangeEntryRepository)repository).isUniqueIndex(any(Document.class));

    // when
    repository.initialize();

    //then
    verify(repository, times(0)).createRequiredUniqueIndex();
    // and not
    verify(repository, times(0)).dropIndex(new Document());
  }

  @Test
  public void shouldCreateDefaultReadWriteConcerns_whenCreating_ifNoParams() {
    //given then
    testReadWriteConcern(WriteConcern.MAJORITY.withJournal(true), ReadConcern.MAJORITY, ReadPreference.primary(), null);
  }


  @Test
  public void shouldThrowException_WhenNoIndexCreation_IfIndexNoPreviouslyCreated() throws MongockException {
  MongockException ex = assertThrows(MongockException.class,()-> initializeRepository(false));
  assertEquals("Index creation not allowed, but not created or wrongly created for collection mongockChangeLog", ex.getMessage());
  }

  @Test
  public void shouldBeOk_WhenNoIndexCreation_IfIndexAlreadyCreated() throws MongockException {
    getDefaultAdapter().createIndex(getIndexDocument(new String[]{"executionId", "author", "changeId"}), new IndexOptions().unique(true));
    initializeRepository(false);
  }

  @Override
  protected MongoDBDriverTestAdapter getAdapter(String collectionName) {
    return new MongoDbReactiveDriverTestAdapterImpl(getDataBase().getCollection(collectionName));
  }


  private void testReadWriteConcern(WriteConcern expectedWriteConcern, ReadConcern expectedReadConcern, ReadPreference expectedReadPreference, ReadWriteConfiguration readWriteConfiguration) {
    MongoReactiveLockRepository repo;
    if(readWriteConfiguration != null) {
      repo = new MongoReactiveLockRepository(getDataBase().getCollection(LOCK_COLLECTION_NAME), readWriteConfiguration);
      repo.setIndexCreation(true);
    } else {
      repo = new MongoReactiveLockRepository(getDataBase().getCollection(LOCK_COLLECTION_NAME));
      repo.setIndexCreation(true);
    }
    MongoCollection<Document> collection = RepositoryAccessorHelper.getCollection(repo).getCollection();
    assertEquals(expectedWriteConcern, collection.getWriteConcern());
    assertEquals(expectedReadConcern, collection.getReadConcern());
    assertEquals(expectedReadPreference, collection.getReadPreference());
  }

  protected Document getIndexDocument(String[] uniqueFields) {
    final Document indexDocument = new Document();
    for (String field : uniqueFields) {
      indexDocument.append(field, 1);
    }
    return indexDocument;
  }

}
