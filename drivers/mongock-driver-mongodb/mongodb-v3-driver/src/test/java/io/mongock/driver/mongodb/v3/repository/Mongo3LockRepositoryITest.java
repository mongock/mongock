package io.mongock.driver.mongodb.v3.repository;


import io.mongock.driver.mongodb.test.template.MongoLockRepositoryITestBase;
import io.mongock.driver.mongodb.test.template.util.MongoDBDriverTestAdapter;
import io.mongock.driver.mongodb.v3.MongoDb3DriverTestAdapterImpl;
import io.mongock.api.exception.MongockException;
import org.bson.Document;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class Mongo3LockRepositoryITest extends MongoLockRepositoryITestBase {

  private static final String LOCK_COLLECTION_NAME = "mongockLock";



  @Test
  public void shouldCreateUniqueIndex_whenEnsureIndex_IfNotCreatedYet() throws MongockException {
    initializeRepository();
    //then
    verify((Mongo3LockRepository)repository, times(1)).createRequiredUniqueIndex();
    // and not
    verify((Mongo3LockRepository)repository, times(0)).dropIndex(any(Document.class));
  }

  @Test
  public void shouldNoCreateUniqueIndex_whenEnsureIndex_IfAlreadyCreated() throws MongockException {
    initializeRepository();
    // given
    repository = Mockito.spy(new Mongo3LockRepository(getDataBase().getCollection(LOCK_COLLECTION_NAME), true));

    doReturn(true).when((Mongo3LockRepository)repository).isUniqueIndex(any(Document.class));

    // when
    repository.initialize();

    //then
    verify((Mongo3LockRepository)repository, times(0)).createRequiredUniqueIndex();
    // and not
    verify((Mongo3LockRepository)repository, times(0)).dropIndex(new Document());
  }

  @Override
  protected void initializeRepository() {
    Mongo3LockRepository repo = new Mongo3LockRepository(getDataBase().getCollection(LOCK_COLLECTION_NAME), true);
    repo.setIndexCreation(true);
    repository = Mockito.spy(repo);
    repository.initialize();
  }

  @Override
  protected MongoDBDriverTestAdapter getAdapter(String collectionName) {
    return new MongoDb3DriverTestAdapterImpl(getDataBase().getCollection(collectionName));
  }
}
