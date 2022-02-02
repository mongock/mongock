package io.mongock.runner.standalone.util;

import com.mongodb.client.MongoClient;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import io.mongock.runner.standalone.MongockStandalone;
import io.mongock.runner.standalone.RunnerStandaloneBuilder;
import io.mongock.util.test.Constants;

import java.util.Arrays;

public class RunnerTestUtil {

  private final MongoClient mongoClient;

  public RunnerTestUtil(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
  }

  public  RunnerStandaloneBuilder getBuilder(String... packagePath) {
    MongoSync4Driver driver = MongoSync4Driver.withDefaultLock(mongoClient, Constants.DEFAULT_DATABASE_NAME);
    return getBuilder(driver, packagePath);
  }

  public  RunnerStandaloneBuilder getBuilder(ConnectionDriver driver, String... packagePath) {
    driver.setMigrationRepositoryName(Constants.CHANGELOG_COLLECTION_NAME);
    return  MongockStandalone.builder()
        .setDriver(driver)
        .addMigrationScanPackages(Arrays.asList(packagePath));
  }
}
