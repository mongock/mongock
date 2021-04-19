package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.SpringDataMongoV3Driver;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.util.Constants;
import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.util.MongoContainer;
import com.github.cloudyrock.springboot.v2_2.MongockSpringbootV2_4;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

abstract class ApplicationRunnerTestBase {

    protected MongoClient mongoClient;
    protected MongoTemplate mongoTemplate;

    protected void start(String mongoVersion) {
        MongoContainer mongoDBContainer = RuntimeTestUtil.startMongoDbContainer(mongoVersion);
        mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl());
        mongoTemplate = new MongoTemplate(mongoClient, RuntimeTestUtil.DEFAULT_DATABASE_NAME);
    }

    protected SpringDataMongoV3Driver buildDriver() {
        SpringDataMongoV3Driver driver = SpringDataMongoV3Driver.withDefaultLock(mongoTemplate);
        driver.setChangeLogRepositoryName(Constants.CHANGELOG_COLLECTION_NAME);
        return driver;
    }

    protected MongockSpringbootV2_4.Builder getBasicBuilder(String packagePath) {
        return MongockSpringbootV2_4.builder()
                .setDriver(buildDriver())
                .addChangeLogsScanPackage(packagePath)
                .setSpringContext(getApplicationContext());
    }

    protected ApplicationContext getApplicationContext() {
        ApplicationContext context = Mockito.mock(ApplicationContext.class);
        Mockito.when(context.getBean(Environment.class)).thenReturn(Mockito.mock(Environment.class));
        return context;
    }
}
