package io.mongock.driver.couchbase.util;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.manager.query.CollectionQueryIndexManager;
import com.couchbase.client.java.manager.query.QueryIndex;
import com.couchbase.client.java.manager.query.QueryIndexManager;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MockClusterBuilder {
  private final String bucketName;
  private final Cluster cluster;
  private final Collection collection;
  private final QueryIndexManager queryIndexManager;
  private final CollectionQueryIndexManager collectionQueryIndexManager;

  public MockClusterBuilder(String bucketName, String scopeName, String collectionName) {
    this.bucketName = bucketName;
    this.cluster = mock(Cluster.class);
    this.collection = mock(Collection.class);
    this.queryIndexManager = mock(QueryIndexManager.class);
    this.collectionQueryIndexManager = mock(CollectionQueryIndexManager.class);
    doReturn(bucketName).when(collection).bucketName();
    doReturn(scopeName).when(collection).scopeName();
    doReturn(collectionName).when(collection).name();
    doReturn(queryIndexManager).when(cluster).queryIndexes();
    doReturn(collectionQueryIndexManager).when(collection).queryIndexes();
    doReturn(Collections.emptyList()).when(queryIndexManager).getAllIndexes(any());
    doNothing().when(queryIndexManager).dropIndex(eq(bucketName), any());
    doNothing().when(queryIndexManager).createIndex(eq(bucketName), any(), any());
    doNothing().when(collectionQueryIndexManager).dropIndex(eq(bucketName), any());
    doNothing().when(collectionQueryIndexManager).createIndex(eq(bucketName), any(), any());
  }

  public Cluster getCluster() {
    return cluster;
  }

  public Collection getCollection() {
    return collection;
  }

  public QueryIndexManager getQueryIndexManager() {
    return queryIndexManager;
  }

  public CollectionQueryIndexManager getCollectionQueryIndexManager() {
    return collectionQueryIndexManager;
  }

  public void addPrimaryKey() {
    QueryIndex queryIndex = mock(QueryIndex.class);
    doReturn(true).when(queryIndex).primary();
    doReturn(bucketName).when(queryIndex).bucketName();
    doReturn(JsonArray.create()).when(queryIndex).indexKey();
    doReturn(Arrays.asList(queryIndex)).when(queryIndexManager).getAllIndexes(any());
    doReturn(Arrays.asList(queryIndex)).when(collectionQueryIndexManager).getAllIndexes();
  }
}
