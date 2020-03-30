package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.utils.IndependentDbIntegrationTestBase;
import org.bson.Document;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @since 10.12.14
 */
public class ChangeEntryRepositoryTest extends IndependentDbIntegrationTestBase {

  private static final String CHANGELOG_COLLECTION_NAME = "dbchangelog";

  @Test
  public void shouldCreateUniqueIndex_whenEnsureIndex_IfNotCreatedYet() throws MongockException {

    // when
    ChangeEntryMongoRepository dao = spy(new ChangeEntryMongoRepository(CHANGELOG_COLLECTION_NAME, db));
    dao.initialize();

    //then
    verify(dao, times(1)).createRequiredUniqueIndex();
    // and not
    verify(dao, times(0)).dropIndex(any(Document.class));
  }

  @Test
  public void shouldNotCreateUniqueIndex_whenEnsureIndex_IfAlreadyCreated() throws MongockException {

    // when

    ChangeEntryMongoRepository dao = spy(new ChangeEntryMongoRepository(CHANGELOG_COLLECTION_NAME, db));
    doCallRealMethod().when(dao).initialize();

    // when
    dao.initialize();
    dao.initialize();

    //then
    verify(dao, times(1)).createRequiredUniqueIndex();//it's only called in the first initialized, as
    //the keys are not created yet. In the second initialize call, keys are already in place, so createRequiredUniqueIndex won't bbe called

    // and not
    verify(dao, times(0)).dropIndex(new Document());
  }

}
