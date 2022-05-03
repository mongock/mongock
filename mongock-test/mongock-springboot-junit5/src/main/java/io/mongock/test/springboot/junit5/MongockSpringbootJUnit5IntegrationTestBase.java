package io.mongock.test.springboot.junit5;

import io.mongock.test.springboot.MongockSpringbootIntegrationTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;


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
