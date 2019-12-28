package com.github.cloudyrock.mongock;

import com.github.cloudyrock.mongock.utils.IndependentDbIntegrationTestBase;
import org.bson.Document;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
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
  private static final String TEST_SERVER = "testServer";
  private static final String DB_NAME = "mongocktest";
  private static final String CHANGELOG_COLLECTION_NAME = "dbchangelog";

  @Test
  public void shouldCreateUniqueIndex_whenEnsureIndex_IfNotCreatedYet() throws MongockException {

    // when
    ChangeEntryRepository dao = spy(new ChangeEntryRepository(CHANGELOG_COLLECTION_NAME, db));
    dao.ensureIndex();

    //then
    verify(dao, times(1)).createRequiredUniqueIndex();
    // and not
    verify(dao, times(0)).dropIndex(any(Document.class));
  }

  @Test
  public void shouldNoCreateUniqueIndex_whenEnsureIndex_IfAlreadyCreated() throws MongockException {

    // when
    ChangeEntryRepository dao = mock(ChangeEntryRepository.class);
    when(dao.findRequiredUniqueIndex()).thenReturn(new Document());
    when(dao.isUniqueIndex(any(Document.class))).thenReturn(true);
    doCallRealMethod().when(dao).ensureIndex();

    // when
    dao.ensureIndex();

    //then
    verify(dao, times(0)).createRequiredUniqueIndex();
    // and not
    verify(dao, times(0)).dropIndex(new Document());
  }

}
