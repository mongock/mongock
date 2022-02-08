package io.mongock.driver.mongodb.sync.v4.repository;


import io.mongock.driver.mongodb.sync.v4.MongoDbSync4DriverTestAdapterImpl;
import io.mongock.driver.mongodb.sync.v4.repository.util.RepositoryAccessorHelper;
import io.mongock.driver.mongodb.test.template.MongoLockRepositoryITestBase;
import io.mongock.driver.mongodb.test.template.interfaces.MongoLockRepositoryITestInterface;
import io.mongock.driver.mongodb.test.template.util.MongoDBDriverTestAdapter;
import io.mongock.api.exception.MongockException;
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


public class MongoSync4LockRepositoryITest extends MongoLockRepositoryITestBase  implements MongoLockRepositoryITestInterface {

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
    MongoSync4LockRepository repo = new MongoSync4LockRepository(getDataBase().getCollection(LOCK_COLLECTION_NAME));
    repo.setIndexCreation(true);
    repository = Mockito.spy(repo);


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
      repo = new MongoSync4LockRepository(getDataBase().getCollection(LOCK_COLLECTION_NAME), readWriteConfiguration);
    } else {
      repo = new MongoSync4LockRepository(getDataBase().getCollection(LOCK_COLLECTION_NAME));
    }
    MongoCollection collection = RepositoryAccessorHelper.getCollection(repo);
    Assert.assertEquals(expectedWriteConcern, collection.getWriteConcern());
    Assert.assertEquals(expectedReadConcern, collection.getReadConcern());
    Assert.assertEquals(expectedReadPreference, collection.getReadPreference());
  }

  @Override
  public void initializeRepository() {
    MongoSync4LockRepository repo = new MongoSync4LockRepository(getDataBase().getCollection(LOCK_COLLECTION_NAME));
    repo.setIndexCreation(true);
    repository = Mockito.spy(repo);
    repository.initialize();
  }

  @Override
  protected MongoDBDriverTestAdapter getAdapter(String collectionName) {
    return new MongoDbSync4DriverTestAdapterImpl(getDataBase().getCollection(collectionName));
  }
}
