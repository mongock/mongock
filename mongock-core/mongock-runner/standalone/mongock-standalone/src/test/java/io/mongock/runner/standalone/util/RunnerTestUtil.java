package io.mongock.runner.standalone.util;

import io.mongock.runner.standalone.MongockStandaloneFixture;
import com.mongodb.client.MongoClient;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import io.mongock.runner.standalone.RunnerStandaloneBuilder;
import io.mongock.util.test.Constants;

import java.util.Arrays;

public class RunnerTestUtil {

  private final MongoClient mongoClient;

  public RunnerTestUtil(MongoClient mongoClient) {
    this.mongoClient = mongoClient;
  }
  
  public  RunnerStandaloneBuilder getBuilder(String... packagePath) {
    return getBuilder(false, packagePath);
  }

  public  RunnerStandaloneBuilder getBuilder(boolean withSystemUpdate, String... packagePath) {
    MongoSync4Driver driver = MongoSync4Driver.withDefaultLock(mongoClient, Constants.DEFAULT_DATABASE_NAME);
    return getBuilder(driver, withSystemUpdate, packagePath);
  }
  
  public  RunnerStandaloneBuilder getBuilder(ConnectionDriver driver, String... packagePath) {
    return getBuilder(driver, false, packagePath);
  }

  public  RunnerStandaloneBuilder getBuilder(ConnectionDriver driver, boolean withSystemUpdate, String... packagePath) {
    driver.setMigrationRepositoryName(Constants.CHANGELOG_COLLECTION_NAME);
    return  MongockStandaloneFixture.builder(withSystemUpdate)
        .setDriver(driver)
        .addMigrationScanPackages(Arrays.asList(packagePath));
  }
}
