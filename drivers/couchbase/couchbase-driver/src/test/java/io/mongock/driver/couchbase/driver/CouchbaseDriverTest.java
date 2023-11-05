package io.mongock.driver.couchbase.driver;

class CouchbaseDriverTest {
//
//  private static final String NONE_DEFAULT_NAME = "nondefault";
//
//  @Test
//  void test_cluster_and_collection_is_set() {
//    // Given
//    Cluster cluster = mockCluster();
//    Collection collection = cluster.bucket("").defaultCollection();
//
//    // when
//    CouchbaseDriver driver = CouchbaseDriver.withDefaultLock(cluster, collection);
//
//    // then
//    CouchbaseRepositoryBase lockRepository = (CouchbaseRepositoryBase) driver.getLockRepository();
//    CouchbaseRepositoryBase changeEntryService = (CouchbaseRepositoryBase) driver.getChangeEntryService();
//    Collection lockRepositoryCollection = (Collection) ReflectionUtils.getPrivateField(lockRepository, CouchbaseRepositoryBase.class, "collection");
//    Collection changeEntryServiceCollection = (Collection) ReflectionUtils.getPrivateField(changeEntryService, CouchbaseRepositoryBase.class,"collection");
//    assertEquals(CollectionIdentifier.DEFAULT_COLLECTION, lockRepositoryCollection.name());
//    assertEquals(CollectionIdentifier.DEFAULT_COLLECTION, changeEntryServiceCollection.name());
//  }
//
//  @Test
//  void test_cluster_and_collection_non_default() {
//    // Given
//    Cluster cluster = mockCluster();
//    Collection collection = cluster.bucket("").scope(NONE_DEFAULT_NAME).collection(NONE_DEFAULT_NAME);
//
//    // when
//    CouchbaseDriver driver = CouchbaseDriver.withDefaultLock(cluster, collection);
//
//    // then
//    CouchbaseRepositoryBase lockRepository = (CouchbaseRepositoryBase) driver.getLockRepository();
//    CouchbaseRepositoryBase changeEntryService = (CouchbaseRepositoryBase) driver.getChangeEntryService();
//    Collection lockRepositoryCollection = (Collection) ReflectionUtils.getPrivateField(lockRepository, CouchbaseRepositoryBase.class, "collection");
//    Collection changeEntryServiceCollection = (Collection) ReflectionUtils.getPrivateField(changeEntryService, CouchbaseRepositoryBase.class,"collection");
//    assertEquals(NONE_DEFAULT_NAME, lockRepositoryCollection.name());
//    assertEquals(NONE_DEFAULT_NAME, changeEntryServiceCollection.name());
//  }
//
//  private Cluster mockCluster() {
//    Cluster cluster = mock(Cluster.class);
//    Bucket bucket = mock(Bucket.class);
//    Scope defaultScope = mock(Scope.class);
//    Scope customeScope = mock(Scope.class);
//    Collection defaultCollection = mock(Collection.class);
//    doReturn(CollectionIdentifier.DEFAULT_COLLECTION).when(defaultCollection).name();
//    Collection customCollection = mock(Collection.class);
//    doReturn(NONE_DEFAULT_NAME).when(customCollection).name();
//    doReturn(bucket).when(cluster).bucket(any());
//    doReturn(defaultScope).when(bucket).defaultScope();
//    doReturn(defaultScope).when(bucket).scope(eq(CollectionIdentifier.DEFAULT_SCOPE));
//    doReturn(customeScope).when(bucket).scope(eq(NONE_DEFAULT_NAME));
//    doReturn(defaultCollection).when(bucket).defaultCollection();
//    doReturn(defaultCollection).when(defaultScope).collection(any());
//    doReturn(customCollection).when(customeScope).collection(any());
//    doReturn(CollectionIdentifier.DEFAULT_SCOPE).when(defaultScope).name();
//    return cluster;
//  }

}
