package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3;

import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.client.initializer.ClientInitializerChangeLog;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.transaction.commitNonFailFast.CommitNonFailFastChangeLog;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.transaction.rollback.RollbackChangeLog;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.transaction.successful.TransactionSuccessfulChangeLog;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.client.ClientRepository;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.util.MongoContainer;
import com.github.cloudyrock.springboot.v2_2.MongockSpringbootV2_4;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.Mongock4Spring5SpringData3App.CLIENTS_COLLECTION_NAME;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// TODO add methodSources to automatize parametrization

@Testcontainers
class SpringApplicationITest {

    private ConfigurableApplicationContext ctx;


    @AfterEach
    void closingSpringApp() {
        if (ctx != null) {
            ctx.close();
            await().atMost(1, TimeUnit.MINUTES)
                    .pollInterval(1, TimeUnit.SECONDS)
                    .until(() -> !ctx.isActive());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"mongo:4.2.6", "mongo:3.6.3"})
    void SpringApplicationShouldRunChangeLogs(String mongoVersion) {
        ctx = RuntimeTestUtil.startSpringAppWithMongoDbVersionAndDefaultPackage(mongoVersion);
        assertEquals(ClientInitializerChangeLog.INITIAL_CLIENTS, ctx.getBean(ClientRepository.class).count());
    }


    @ParameterizedTest
    @ValueSource(strings = {"mongo:4.2.6"})
    void ApplicationRunnerShouldBeInjected(String mongoVersion) {
        ctx = RuntimeTestUtil.startSpringAppWithMongoDbVersionAndDefaultPackage(mongoVersion);
        ctx.getBean(MongockSpringbootV2_4.MongockApplicationRunner.class);
    }


    @ParameterizedTest
    @ValueSource(strings = {"mongo:4.2.6"})
    void ApplicationRunnerShouldNotBeInjected_IfDisabledByProperties(String mongoVersion) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("mongock.enabled", "false");
        parameters.put("mongock.changeLogsScanPackage", "com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.changelogs.client");
        parameters.put("mongock.transactionable", "false");
        ctx = RuntimeTestUtil.startSpringAppWithMongoDbVersionAndParameters(mongoVersion, parameters);
        Exception ex = assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> ctx.getBean(MongockSpringbootV2_4.MongockApplicationRunner.class));
        assertEquals(
                "No qualifying bean of type 'com.github.cloudyrock.springboot.v2_2.MongockSpringbootV2_4$MongockApplicationRunner' available",
                ex.getMessage()
        );
    }


    @ParameterizedTest
    @ValueSource(strings = {"mongo:4.2.6"})
    void InitializingBeanShouldNotBeInjected(String mongoVersion) {
        ctx = RuntimeTestUtil.startSpringAppWithMongoDbVersionAndDefaultPackage(mongoVersion);
        Exception ex = assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> ctx.getBean(MongockSpringbootV2_4.MongockInitializingBeanRunner.class),
                "MongockInitializingBeanRunner should not be injected to the context as runner-type is not set");
        assertEquals(
                "No qualifying bean of type 'com.github.cloudyrock.springboot.v2_2.MongockSpringbootV2_4$MongockInitializingBeanRunner' available",
                ex.getMessage()
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"mongo:4.2.6"})
    void shouldThrowExceptionWhenScanPackageNotSpecified(String mongoVersion) {
        Exception ex = assertThrows(
                IllegalStateException.class,
                () -> RuntimeTestUtil.startSpringAppWithMongoDbVersionAndNoPackage(mongoVersion));
        assertEquals(MongockException.class, ex.getCause().getClass());
        assertEquals("changeLogsScanPackage must be injected to Mongock builder", ex.getCause().getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"mongo:4.2.6"})
    void shouldRollBack_IfTransaction_WhenExceptionInChangeLog(String mongoDbVersion) {
        MongoContainer mongoContainer = RuntimeTestUtil.startMongoDbContainer(mongoDbVersion);
        MongoCollection clientsCollection = MongoClients.create(mongoContainer.getReplicaSetUrl()).getDatabase(RuntimeTestUtil.DEFAULT_DATABASE_NAME).getCollection(CLIENTS_COLLECTION_NAME);
        try {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("mongock.changeLogsScanPackage", RollbackChangeLog.class.getPackage().getName());
            ctx = RuntimeTestUtil.startSpringAppWithParameters(mongoContainer, parameters);
        } catch (Exception ex) {
            //ignore
        }

        // then
        long actual = clientsCollection.countDocuments();
        assertEquals(0, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"mongo:4.2.6"})
    void shouldCommit_IfTransaction_WhenChangeLogOK(String mongoDbVersion) {
        ctx = RuntimeTestUtil.startSpringAppWithMongoDbVersionAndPackage(mongoDbVersion, TransactionSuccessfulChangeLog.class.getPackage().getName());

        // then
        assertEquals(10, ctx.getBean(ClientRepository.class).count());
    }

    @ParameterizedTest
    @ValueSource(strings = {"mongo:4.2.6"})
    void shouldCommit_IfChangeLogFail_WhenNonFailFast(String mongoDbVersion) {
        ctx = RuntimeTestUtil.startSpringAppWithMongoDbVersionAndPackage(mongoDbVersion, CommitNonFailFastChangeLog.class.getPackage().getName());

        // then
        assertEquals(10, ctx.getBean(ClientRepository.class).count());
    }

    @ParameterizedTest
    @ValueSource(strings = {"mongo:4.2.6"})
    void shouldNotExecuteTransaction_IfConfigurationTransactionDisabled(String mongoDbVersion) {
        ctx = RuntimeTestUtil.startSpringAppWithTransactionDisabledMongoDbVersionAndPackage(mongoDbVersion, CommitNonFailFastChangeLog.class.getPackage().getName());

        // then
        assertEquals(10, ctx.getBean(ClientRepository.class).count());
    }


}
