package io.mongock.driver.couchbase.repository;

import com.couchbase.client.core.io.CollectionIdentifier;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.couchbase.util.MockClusterBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CouchbaseRepositoryBaseTest {
  private static final String BUCKET_NAME = "mongock";
  private static final String SCOPE_NAME = "scope";
  private static final String COLLECTION_NAME = "collection";

  @Test
  void test_ensureIndex_index_created_on_cluster() {
    // given
    MockClusterBuilder clusterInfo = new MockClusterBuilder(BUCKET_NAME, CollectionIdentifier.DEFAULT_SCOPE, CollectionIdentifier.DEFAULT_COLLECTION);
    CouchbaseChangeEntryRepository repo = new CouchbaseChangeEntryRepository(clusterInfo.getCluster(), clusterInfo.getCollection());
    repo.setIndexCreation(true);
    
    // when
    repo.initialize();
    
    // then
    verify(clusterInfo.getQueryIndexManager(), times(1)).dropIndex(any(), any(), any());
    verify(clusterInfo.getQueryIndexManager(), times(1)).createIndex(eq(BUCKET_NAME), any(), any(), any());
  }

  @Test
  void test_ensureIndex_throw_exception_if_no_primary_and_index_creation_false_on_cluster() {
    // given
    MockClusterBuilder clusterInfo = new MockClusterBuilder(BUCKET_NAME, CollectionIdentifier.DEFAULT_SCOPE, CollectionIdentifier.DEFAULT_COLLECTION);
    CouchbaseChangeEntryRepository repo = new CouchbaseChangeEntryRepository(clusterInfo.getCluster(), clusterInfo.getCollection());
    repo.setIndexCreation(false);

    // when
    assertThrows(MongockException.class, repo::initialize);

    // then
    verify(clusterInfo.getQueryIndexManager(), never()).dropIndex(any(), any(), any());
    verify(clusterInfo.getQueryIndexManager(), never()).createIndex(eq(BUCKET_NAME), any(), any(), any());
  }

  @Test
  void test_ensureIndex_do_nothing_if_primary_exists_and_index_creation_false_on_cluster() {
    // given
    MockClusterBuilder clusterInfo = new MockClusterBuilder(BUCKET_NAME, CollectionIdentifier.DEFAULT_SCOPE, CollectionIdentifier.DEFAULT_COLLECTION);
    clusterInfo.addPrimaryKey();
    CouchbaseChangeEntryRepository repo = new CouchbaseChangeEntryRepository(clusterInfo.getCluster(), clusterInfo.getCollection());
    repo.setIndexCreation(false);

    // when
    repo.initialize();

    // then
    verify(clusterInfo.getQueryIndexManager(), never()).dropIndex(any(), any(), any());
    verify(clusterInfo.getQueryIndexManager(), never()).createIndex(eq(BUCKET_NAME), any(), any(), any());
  }

  @Test
  void test_ensureIndex_index_created_on_collection() {
    // given
    MockClusterBuilder clusterInfo = new MockClusterBuilder(BUCKET_NAME, SCOPE_NAME, COLLECTION_NAME);
    CouchbaseChangeEntryRepository repo = new CouchbaseChangeEntryRepository(clusterInfo.getCluster(), clusterInfo.getCollection());
    repo.setIndexCreation(true);

    // when
    repo.initialize();

    // then
    verify(clusterInfo.getCollectionQueryIndexManager(), times(1)).dropIndex(any(), any());
    verify(clusterInfo.getCollectionQueryIndexManager(), times(1)).createIndex(any(), any(), any());
  }

  @Test
  void test_ensureIndex_throw_exception_if_no_primary_and_index_creation_false_on_collection() {
    // given
    MockClusterBuilder clusterInfo = new MockClusterBuilder(BUCKET_NAME, SCOPE_NAME, COLLECTION_NAME);
    CouchbaseChangeEntryRepository repo = new CouchbaseChangeEntryRepository(clusterInfo.getCluster(), clusterInfo.getCollection());
    repo.setIndexCreation(false);

    // when
    assertThrows(MongockException.class, repo::initialize);

    // then
    verify(clusterInfo.getCollectionQueryIndexManager(), never()).dropIndex(any(), any());
    verify(clusterInfo.getCollectionQueryIndexManager(), never()).createIndex(any(), any(), any());
  }

  @Test
  void test_ensureIndex_do_nothing_if_primary_exists_and_index_creation_false_on_collection() {
    // given
    MockClusterBuilder clusterInfo = new MockClusterBuilder(BUCKET_NAME, SCOPE_NAME, COLLECTION_NAME);
    clusterInfo.addPrimaryKey();
    CouchbaseChangeEntryRepository repo = new CouchbaseChangeEntryRepository(clusterInfo.getCluster(), clusterInfo.getCollection());
    repo.setIndexCreation(false);

    // when
    repo.initialize();

    // then
    verify(clusterInfo.getCollectionQueryIndexManager(), never()).dropIndex(any(), any());
    verify(clusterInfo.getCollectionQueryIndexManager(), never()).createIndex(any(), any(), any());
  }

}
