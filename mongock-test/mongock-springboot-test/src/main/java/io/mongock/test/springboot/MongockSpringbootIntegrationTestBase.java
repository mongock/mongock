package io.mongock.test.springboot;

import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.core.driver.ConnectionDriverBase;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.test.core.MongockIntegrationTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;


@TestPropertySource(properties = {"mongock.runner-type=NONE"})
public class MongockSpringbootIntegrationTestBase extends MongockIntegrationTestBase {

  /**
   * It marks the ConnectionDriver to allow re-initialization(which is not allowed in production) and
   * build the runner
   */
  @Autowired
  public void runnerBuilder(RunnerBuilder builder) {
    setBuilder(builder);
  }

  /**
   * It cleans both repositories, lock and changeLogs.
   * Notice that currently it's doing nothing(until those methods are implemented in ConnectionDriver interface)
   */
  @Autowired
  public void connectionDriver(ConnectionDriver driver) {
    setConnectionDriver((ConnectionDriverBase) driver);
  }

}
