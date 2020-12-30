package com.github.cloudyrock.mongock.driver.mongodb.sync.v4.repository;

import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.MongoDbSync4DriverTestAdapterImpl;
import com.github.cloudyrock.mongock.driver.mongodb.test.template.MongoChangeEntryRepositoryITestBase;
import com.github.cloudyrock.mongock.driver.mongodb.test.template.util.MongoDBDriverTestAdapter;
import com.github.cloudyrock.mongock.exception.MongockException;
import org.bson.Document;
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

  @Override
  protected MongoDBDriverTestAdapter getAdapter(String collectionName) {
    return new MongoDbSync4DriverTestAdapterImpl(getDataBase().getCollection(collectionName));
  }
}
