package com.github.cloudyrock.mongock.driver.mongodb.v3.repository;

import com.github.cloudyrock.mongock.driver.mongodb.v3.driver.util.IntegrationTestBase;
import io.changock.migration.api.exception.ChangockException;
import org.bson.Document;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class MongoChangeEntryRepositoryTest extends IntegrationTestBase {

  private static final String CHANGELOG_COLLECTION_NAME = "dbchangelog";
  private MongoLockRepository repository;

  @Test
  public void shouldCreateUniqueIndex_whenEnsureIndex_IfNotCreatedYet() throws ChangockException {
    collection = getDataBase().getCollection(CHANGELOG_COLLECTION_NAME);
    repository = Mockito.spy(new MongoLockRepository(collection));
    repository.initialize();

    //then
    verify(repository, times(1)).createRequiredUniqueIndex();
    // and not
    verify(repository, times(0)).dropIndex(any(Document.class));
  }

  @Test
  public void shouldNoCreateUniqueIndex_whenEnsureIndex_IfAlreadyCreated() throws ChangockException {
    // given
    collection = getDataBase().getCollection(CHANGELOG_COLLECTION_NAME);
    repository = Mockito.spy(new MongoLockRepository(collection));

    doReturn(true).when(repository).isUniqueIndex(any(Document.class));

    // when
    repository.initialize();

    //then
    verify(repository, times(0)).createRequiredUniqueIndex();
    // and not
    verify(repository, times(0)).dropIndex(new Document());
  }


}
