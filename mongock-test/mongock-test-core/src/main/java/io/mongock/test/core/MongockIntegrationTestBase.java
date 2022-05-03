package io.mongock.test.core;

import io.mongock.driver.core.driver.ConnectionDriverBase;
import io.mongock.driver.core.driver.ConnectionDriverTestingWrapper;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.executor.MongockRunner;

public abstract class MongockIntegrationTestBase {

  private RunnerBuilder builder;
  private ConnectionDriverTestingWrapper connectionDriverTestingWrapper;
  private MongockRunner mongockRunner;

  protected void setBuilder(RunnerBuilder builder) {
    this.builder = builder;
  }

  protected void setConnectionDriver(ConnectionDriverBase connectionDriver) {
    connectionDriverTestingWrapper = new ConnectionDriverTestingWrapper(connectionDriver);
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
