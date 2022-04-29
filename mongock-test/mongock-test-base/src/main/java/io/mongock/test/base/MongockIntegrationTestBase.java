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

  public void mongockBeforeEach() {
    connectionDriverTestingWrapper.reset();
    mongockRunner = builder.buildRunner();
  }

  public void mongockAfterEach() {
    connectionDriverTestingWrapper.cleanMigrationRepository();
    connectionDriverTestingWrapper.cleanMigrationRepository();
  }

  public void executeMongock() {
    mongockRunner.execute();
  }

}
