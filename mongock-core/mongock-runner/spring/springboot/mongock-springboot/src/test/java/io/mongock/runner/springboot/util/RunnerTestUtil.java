package io.mongock.runner.springboot.util;

import io.mongock.runner.springboot.MongockSpringbootFixture;
import com.mongodb.client.MongoClient;
import io.mongock.driver.mongodb.springdata.v3.SpringDataMongoV3Driver;
import io.mongock.runner.springboot.RunnerSpringbootBuilder;
import io.mongock.util.test.Constants;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Arrays;

public class RunnerTestUtil {

  private final MongoTemplate mongoTemplate;

  public RunnerTestUtil(MongoClient mongoClient) {
    this.mongoTemplate = new MongoTemplate(mongoClient, Constants.DEFAULT_DATABASE_NAME);
  }

  public RunnerSpringbootBuilder getRunner(String... packagePath) {
    return getRunner(false, packagePath);
  }
  
  public RunnerSpringbootBuilder getRunner(boolean withSystemUpdate, String... packagePath) {
    return getRunnerInternal(false, withSystemUpdate, packagePath);
  }
  
  public RunnerSpringbootBuilder getRunnerTransactional(String... packagePath) {
    return getRunnerTransactional(false, packagePath);
  }

  public RunnerSpringbootBuilder getRunnerTransactional(boolean withSystemUpdate, String... packagePath) {
    return getRunnerInternal(true, withSystemUpdate, packagePath)
        .setTransactionEnabled(true);
  }

  private RunnerSpringbootBuilder getRunnerInternal(boolean transactional, boolean withSystemUpdate, String... packagePath) {
    SpringDataMongoV3Driver driver = SpringDataMongoV3Driver.withDefaultLock(mongoTemplate);
    if(transactional) {
      driver.enableTransaction();
    }
    driver.setMigrationRepositoryName(Constants.CHANGELOG_COLLECTION_NAME);
    ApplicationContext context = Mockito.mock(ApplicationContext.class);
    Mockito.when(context.getBean(MongoTemplate.class)).thenReturn(mongoTemplate);
    Mockito.when(context.getBean(Environment.class)).thenReturn(Mockito.mock(Environment.class));
    Mockito.when(context.getEnvironment()).thenReturn(Mockito.mock(Environment.class));

    return  MongockSpringbootFixture.builder(withSystemUpdate)
        .setDriver(driver)
        .setSpringContext(context)

        .addMigrationScanPackages(Arrays.asList(packagePath));
  }
}
