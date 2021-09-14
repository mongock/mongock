package io.mongock.integrationtests.spring5.springdata3;

import io.mongock.driver.mongodb.springdata.v3.SpringDataMongoV3Driver;
import io.mongock.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import io.mongock.integrationtests.spring5.springdata3.util.Constants;
import io.mongock.integrationtests.spring5.springdata3.util.MongoContainer;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.mongock.runner.springboot.RunnerSpringbootBuilder;
import io.mongock.runner.springboot.MongockSpringboot;
import io.mongock.runner.standalone.RunnerStandaloneBuilder;
import io.mongock.runner.standalone.MongockStandalone;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Arrays;

abstract class ApplicationRunnerTestBase {

  protected MongoClient mongoClient;
  protected MongoTemplate mongoTemplate;

  protected void start(String mongoVersion) {
    MongoContainer mongoDBContainer = RuntimeTestUtil.startMongoDbContainer(mongoVersion);
    mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl());
    mongoTemplate = new MongoTemplate(mongoClient, RuntimeTestUtil.DEFAULT_DATABASE_NAME);
  }


  protected RunnerSpringbootBuilder getSpringBootBuilderWithSpringData(String... packagePath) {
    SpringDataMongoV3Driver driver = SpringDataMongoV3Driver.withDefaultLock(mongoTemplate);
    driver.setChangeLogRepositoryName(Constants.CHANGELOG_COLLECTION_NAME);
    return MongockSpringboot.builder()
        .setDriver(driver)
        .addChangeLogsScanPackages(Arrays.asList(packagePath))
        .setSpringContext(getApplicationContext());
  }


  protected RunnerStandaloneBuilder getStandaloneBuilderWithMongoDBSync4(String... packagePath) {
    MongoSync4Driver driver = MongoSync4Driver.withDefaultLock(mongoClient, RuntimeTestUtil.DEFAULT_DATABASE_NAME);
    driver.setChangeLogRepositoryName(Constants.CHANGELOG_COLLECTION_NAME);
    return  MongockStandalone.builder()
        .setDriver(driver)
        .addChangeLogsScanPackages(Arrays.asList(packagePath));
  }


  protected ApplicationContext getApplicationContext() {
    ApplicationContext context = Mockito.mock(ApplicationContext.class);
    Mockito.when(context.getBean(Environment.class)).thenReturn(Mockito.mock(Environment.class));
    return context;
  }
}
