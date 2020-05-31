package com.github.cloudyrock.mongock.driver.mongodb.v3.repository;


import io.changock.migration.api.exception.ChangockException;
import org.bson.Document;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class Mongo3LockRepositoryITest extends Mongo3LockRepositoryITestBase {

  private static final String LOCK_COLLECTION_NAME = "changockLock";



  @Test
  public void shouldCreateUniqueIndex_whenEnsureIndex_IfNotCreatedYet() throws ChangockException {
    initializeRepository();
    //then
    verify((Mongo3LockRepository)repository, times(1)).createRequiredUniqueIndex();
    // and not
    verify((Mongo3LockRepository)repository, times(0)).dropIndex(any(Document.class));
  }

  @Test
  public void shouldNoCreateUniqueIndex_whenEnsureIndex_IfAlreadyCreated() throws ChangockException {
    initializeRepository();
    // given
    collection = getDataBase().getCollection(LOCK_COLLECTION_NAME);
    repository = Mockito.spy(new Mongo3LockRepository(collection, true));

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
    repository = Mockito.spy(new Mongo3LockRepository(collection, true));
    repository.initialize();
  }
}
