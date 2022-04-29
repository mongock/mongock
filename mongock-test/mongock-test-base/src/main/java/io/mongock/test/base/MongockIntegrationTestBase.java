package io.mongock.test.base;

import io.mongock.driver.core.driver.ConnectionDriverBase;
import io.mongock.driver.core.driver.ConnectionDriverTestingWrapper;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.executor.MongockRunner;

public abstract class MongockIntegrationTestBase {

  protected RunnerBuilder builder;
  protected ConnectionDriverTestingWrapper connectionDriverTestingWrapper;
  protected MongockRunner mongockRunner;

  public MongockIntegrationTestBase() {
  }

  public MongockIntegrationTestBase(RunnerBuilder builder) {
    this.builder = builder;
    connectionDriverTestingWrapper = new ConnectionDriverTestingWrapper((ConnectionDriverBase) builder.getDriver());
  }

  /**
   * It marks the ConnectionDriver to allow re-initialization(which is not allowed in production) and
   * build the runner
   */
  public void mongockBeforeEach() {
    connectionDriverTestingWrapper.reset();
    mongockRunner = builder.buildRunner();
  }

  /**
   * It cleans both repositories, lock and changeLogs.
   * Notice that currently it's doing nothing(until those methods are implemented in ConnectionDriver interface)
   */
  public void mongockAfterEach() {
    connectionDriverTestingWrapper.cleanMigrationRepository();
    connectionDriverTestingWrapper.cleanMigrationRepository();
  }

  /**
   * It executes the runner
   */
  public void executeMongock() {
    mongockRunner.execute();
  }

}
