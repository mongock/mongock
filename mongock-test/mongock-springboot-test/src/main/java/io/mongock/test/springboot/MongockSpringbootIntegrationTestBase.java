package io.mongock.test.springboot;

import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.core.driver.ConnectionDriverBase;
import io.mongock.driver.core.driver.ConnectionDriverTestingWrapper;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.test.base.MongockIntegrationTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;


@TestPropertySource(properties = {"mongock.runner-type=NONE"})
public class MongockSpringbootIntegrationTestBase extends MongockIntegrationTestBase {


  @Autowired
  public void runnerBuilder(RunnerBuilder builder) {
    this.builder = builder;
  }

  @Autowired
  public void connectionDriver(ConnectionDriver driver) {
    connectionDriverTestingWrapper = new ConnectionDriverTestingWrapper((ConnectionDriverBase) driver);
  }

}
