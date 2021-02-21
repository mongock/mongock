package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.repository;


import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.MongoDbSync4DriverTestAdapterImpl;
import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.repository.util.RepositoryAccessor;
import com.github.cloudyrock.mongock.driver.mongodb.test.template.MongoLockRepositoryITestBase;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class MongoSync4LockRepositoryITest extends MongoLockRepositoryITestBase {

  private static final String LOCK_COLLECTION_NAME = "mongockLock";



  @Test
  public void shouldCreateUniqueIndex_whenEnsureIndex_IfNotCreatedYet() throws MongockException {
    initializeRepository();
    //then
    verify((MongoSync4LockRepository)repository, times(1)).createRequiredUniqueIndex();
    // and not
    verify((MongoSync4LockRepository)repository, times(0)).dropIndex(any(Document.class));
  }

  @Test
  public void shouldNoCreateUniqueIndex_whenEnsureIndex_IfAlreadyCreated() throws MongockException {
    initializeRepository();

    // given
    //TODO remove this. Already done in initializeRepository()
    repository = Mockito.spy(new MongoSync4LockRepository(getDataBase().getCollection(LOCK_COLLECTION_NAME), true));

    doReturn(true).when((MongoSync4LockRepository)repository).isUniqueIndex(any(Document.class));

    // when
    repository.initialize();

    //then
    verify((MongoSync4LockRepository)repository, times(0)).createRequiredUniqueIndex();
    // and not
    verify((MongoSync4LockRepository)repository, times(0)).dropIndex(new Document());
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
  protected void initializeRepository() {
    repository = Mockito.spy(new MongoSync4LockRepository(getDataBase().getCollection(LOCK_COLLECTION_NAME), true));
    repository.initialize();
  }

  @Override
  protected MongoDBDriverTestAdapter getAdapter(String collectionName) {
    return new MongoDbSync4DriverTestAdapterImpl(getDataBase().getCollection(collectionName));
  }
}
