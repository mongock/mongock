package io.mongock.driver.couchbase.springboot.config;

import com.couchbase.client.core.io.CollectionIdentifier;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import io.mongock.api.config.MongockConfiguration;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.couchbase.driver.CouchbaseDriver;
import io.mongock.runner.springboot.base.config.MongockSpringConfiguration;
import io.mongock.util.test.ReflectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.couchbase.SimpleCouchbaseClientFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CouchbaseSpringbootContextTest {

    private static final String NONE_DEFAULT_NAME = "nondefault";
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    void test_driver_autoconfigured() {
        contextRunner
                .withPropertyValues("mongock.enabled=true")
                .withBean(SimpleCouchbaseClientFactory.class, mockCluster(), "mongock", null)
                .withConfiguration(AutoConfigurations.of(
                        MongockConfiguration.class,
                        CouchbaseSpringbootContext.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(ConnectionDriver.class);
                    CouchbaseDriver driver = context.getBean(CouchbaseDriver.class);
                    assertThat(collectionName(driver)).isEqualTo(CollectionIdentifier.DEFAULT_COLLECTION);
                    assertThat(driver.isIndexCreation()).isTrue();
                }
                );
    }

    @Test
    void test_driver_missing_on_mongock_disabled() {
        contextRunner
                .withPropertyValues("mongock.enabled=false")
                .withBean(SimpleCouchbaseClientFactory.class, mockCluster(), "mongock", null)
                .withConfiguration(AutoConfigurations.of(
                        MongockConfiguration.class,
                        CouchbaseSpringbootContext.class))
                .run((context) -> assertThat(context)
                .doesNotHaveBean(ConnectionDriver.class));
    }

    @Test
    void test_context_collection_modifications() {
        contextRunner
                .withPropertyValues("mongock.enabled=true", "mongock.couchbase.scope=nondefault", "mongock.couchbase.collection=nondefault")
                .withBean(SimpleCouchbaseClientFactory.class, mockCluster(), "mongock", null)
                .withConfiguration(AutoConfigurations.of(
                        MongockConfiguration.class,
                        CouchbaseSpringbootContext.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(ConnectionDriver.class);
                    CouchbaseDriver driver = context.getBean(CouchbaseDriver.class);
                    assertThat(collectionName(driver)).isEqualTo(NONE_DEFAULT_NAME);
                    assertThat(driver.isIndexCreation()).isTrue();
                }
                );
    }

    @Test
    void test_context_index_creation_disabled() {
        contextRunner
                .withPropertyValues("mongock.enabled=true", "mongock.index-creation=false", "mongock.indexCreation=false")
                .withBean(SimpleCouchbaseClientFactory.class, mockCluster(), "mongock", null)
                .withUserConfiguration(
                        MongockSpringConfiguration.class,
                        CouchbaseSpringbootContext.class
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(ConnectionDriver.class);
                    CouchbaseDriver driver = context.getBean(CouchbaseDriver.class);
                    assertThat(driver.isIndexCreation()).isFalse();
                }
                );
    }

    private String collectionName(ConnectionDriver connectionDriver) {
        Collection collection = (Collection) ReflectionUtils.getPrivateField(connectionDriver, CouchbaseDriver.class, "collection");
        return collection.name();
    }

    private Cluster mockCluster() {
        Cluster cluster = mock(Cluster.class);
        Bucket bucket = mock(Bucket.class);
        Scope defaultScope = mock(Scope.class);
        Scope customeScope = mock(Scope.class);
        Collection defaultCollection = mock(Collection.class);
        doReturn(CollectionIdentifier.DEFAULT_COLLECTION).when(defaultCollection).name();
        Collection customCollection = mock(Collection.class);
        doReturn(NONE_DEFAULT_NAME).when(customCollection).name();
        doReturn(bucket).when(cluster).bucket(any());
        doReturn(defaultScope).when(bucket).defaultScope();
        doReturn(defaultScope).when(bucket).scope(eq(CollectionIdentifier.DEFAULT_SCOPE));
        doReturn(customeScope).when(bucket).scope(eq(NONE_DEFAULT_NAME));
        doReturn(defaultCollection).when(bucket).defaultCollection();
        doReturn(defaultCollection).when(defaultScope).collection(any());
        doReturn(customCollection).when(customeScope).collection(any());
        doReturn(CollectionIdentifier.DEFAULT_SCOPE).when(defaultScope).name();
        return cluster;
    }

}
