package io.mongock.driver.couchbase;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;

import java.time.Duration;

public abstract class TestcontainersCouchbaseRunner {
    private static final String COUCHBASE_CUSTOM_SCOPE = "mongock";
    private static final String COUCHBASE_CUSTOM_COLLECTION = "mongock";
    protected static final String BUCKET_NAME = "mongock";
    protected static final String COUCHBASE_USERNAME = "username";
    protected static final String COUCHBASE_PASSWORD = "password";
    protected static final BucketDefinition BUCKET_DEFINITION = new BucketDefinition(BUCKET_NAME);
    private static Cluster CLUSTER_V7;
    private static Collection COLLECTION_V7;
    private static Bucket BUCKET_V7;
    private static Cluster CLUSTER_V6;
    private static Bucket BUCKET_V6;
    private static Collection COLLECTION_V6;

    private static final CouchbaseContainer COUCHBASE_CONTAINER_V7 = new CouchbaseContainer("couchbase/server:community-7.1.1")
            .withBucket(BUCKET_DEFINITION)
            .withCredentials(COUCHBASE_USERNAME, COUCHBASE_PASSWORD)
            .withStartupTimeout(Duration.ofSeconds(90))
            .waitingFor(Wait.forHealthcheck());

    private static final CouchbaseContainer COUCHBASE_CONTAINER_V6 = new CouchbaseContainer("couchbase/server:community-6.6.0")
            .withBucket(BUCKET_DEFINITION)
            .withCredentials(COUCHBASE_USERNAME, COUCHBASE_PASSWORD)
            .withStartupTimeout(Duration.ofSeconds(90))
            .waitingFor(Wait.forHealthcheck());

    static {
        COUCHBASE_CONTAINER_V7.withStartupAttempts(3).start();
        Cluster cluster = Cluster.connect(COUCHBASE_CONTAINER_V7.getConnectionString(), ClusterOptions.clusterOptions(COUCHBASE_USERNAME, COUCHBASE_PASSWORD));
        cluster.bucket(BUCKET_NAME).collections().createScope(COUCHBASE_CUSTOM_SCOPE);
        cluster.bucket(BUCKET_NAME).collections().createCollection(CollectionSpec.create(COUCHBASE_CUSTOM_COLLECTION, COUCHBASE_CUSTOM_SCOPE));
        cluster.disconnect();
        COUCHBASE_CONTAINER_V6.withStartupAttempts(3).start();
    }

    public static Cluster getCluster7() {
        if (CLUSTER_V7 == null) {
            CLUSTER_V7 = Cluster.connect(
                    COUCHBASE_CONTAINER_V7.getConnectionString(),
                    COUCHBASE_USERNAME,
                    COUCHBASE_PASSWORD
            );
        }
        return CLUSTER_V7;
    }

    public static Collection getCollectionV7() {
        if (COLLECTION_V7 == null) {
            COLLECTION_V7 = getBucketV7()
                    .scope(COUCHBASE_CUSTOM_SCOPE)
                    .collection(COUCHBASE_CUSTOM_COLLECTION);
        }
        return COLLECTION_V7;
    }

    public static Cluster getCluster6() {
        if (CLUSTER_V6 == null) {
            CLUSTER_V6 = Cluster.connect(
                    COUCHBASE_CONTAINER_V6.getConnectionString(),
                    COUCHBASE_USERNAME,
                    COUCHBASE_PASSWORD
            );
        }
        return CLUSTER_V6;
    }

    public static Collection getCollectionV6() {
        if (COLLECTION_V6 == null) {
            COLLECTION_V6 = getBucketV6()
                    .defaultCollection();
        }
        return COLLECTION_V6;
    }

    public static Bucket getBucketV6() {
        if (BUCKET_V6 == null) {
            BUCKET_V6 = getCluster6()
                    .bucket(BUCKET_NAME);
        }
        return BUCKET_V6;
    }

    public static Bucket getBucketV7() {
        if (BUCKET_V7 == null) {
            BUCKET_V7 = getCluster7()
                    .bucket(BUCKET_NAME);
        }
        return BUCKET_V7;
    }

}
