package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.repository;

import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.MongoDbSync4DriverTestAdapterImpl;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.repository.util.RepositoryAccessor;
import com.github.cloudyrock.mongock.driver.mongodb.test.template.MongoChangeEntryRepositoryITestBase;
import com.github.cloudyrock.mongock.driver.mongodb.test.template.util.MongoDBDriverTestAdapter;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class MongoSync4ChangeEntryRepositoryITest extends MongoChangeEntryRepositoryITestBase {


  protected void initializeRepository(boolean indexCreation) {
    repository = Mockito.spy(new MongoSync4ChangeEntryRepository<>(getDataBase().getCollection(CHANGELOG_COLLECTION_NAME), indexCreation));
    repository.initialize();
  }

  @Test
  public void shouldCreateUniqueIndex_whenEnsureIndex_IfNotCreatedYet() throws MongockException {
    initializeRepository(true);

    //then
    verify((MongoSync4ChangeEntryRepository)repository, times(1)).createRequiredUniqueIndex();
    // and not
    verify((MongoSync4ChangeEntryRepository)repository, times(0)).dropIndex(any(Document.class));
  }

  @Test
  public void shouldNoCreateUniqueIndex_whenEnsureIndex_IfAlreadyCreated() throws MongockException {
    initializeRepository(true);
    // given
    repository = Mockito.spy(new MongoSync4ChangeEntryRepository(getDataBase().getCollection(CHANGELOG_COLLECTION_NAME), true));

    doReturn(true).when((MongoSync4ChangeEntryRepository)repository).isUniqueIndex(any(Document.class));

    // when
    repository.initialize();

    //then
    verify((MongoSync4ChangeEntryRepository)repository, times(0)).createRequiredUniqueIndex();
    // and not
    verify((MongoSync4ChangeEntryRepository)repository, times(0)).dropIndex(new Document());
  }

  //TODO THIS SHOULD BE MOVED TO MongoChangeEntryRepositoryITestBase FOR REUSE
  @Test
  public void shouldCreateDefaultReadWriteConcerns_whenCreating_ifNoParams() {
    //given then
    testReadWriteConcern(WriteConcern.MAJORITY.withJournal(true), ReadConcern.MAJORITY, ReadPreference.primary(), null);
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

  private void testReadWriteConcern(WriteConcern expectedWriteConcern, ReadConcern expectedReadConcern, ReadPreference expectedReadPreference, ReadWriteConfiguration readWriteConfiguration) {
    MongoSync4LockRepository repo;
    if(readWriteConfiguration != null) {
      repo = new MongoSync4LockRepository(getDataBase().getCollection(LOCK_COLLECTION_NAME), true, readWriteConfiguration);
    } else {
      repo = new MongoSync4LockRepository(getDataBase().getCollection(LOCK_COLLECTION_NAME), true);
    }
    MongoCollection collection = RepositoryAccessor.getCollection(repo);
    Assert.assertEquals(expectedWriteConcern, collection.getWriteConcern());
    Assert.assertEquals(expectedReadConcern, collection.getReadConcern());
    Assert.assertEquals(expectedReadPreference, collection.getReadPreference());
  }



  @Override
  protected MongoDBDriverTestAdapter getAdapter(String collectionName) {
    return new MongoDbSync4DriverTestAdapterImpl(getDataBase().getCollection(collectionName));
  }
}
