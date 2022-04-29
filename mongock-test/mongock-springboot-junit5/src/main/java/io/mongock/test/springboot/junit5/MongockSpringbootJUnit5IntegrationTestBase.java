package io.mongock.test.springboot.junit5;

import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.core.driver.ConnectionDriverBase;
import io.mongock.driver.core.driver.ConnectionDriverTestingWrapper;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.test.base.MongockIntegrationTestBase;
import io.mongock.test.springboot.MongockSpringbootIntegrationTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;


public class MongockSpringbootJUnit5IntegrationTestBase extends MongockSpringbootIntegrationTestBase {

  @Override
  @BeforeEach
  public void mongockBeforeEach() {
    super.mongockBeforeEach();
  }

  @Override
  @AfterEach
  public void mongockAfterEach() {
    super.mongockAfterEach();
  }
}
