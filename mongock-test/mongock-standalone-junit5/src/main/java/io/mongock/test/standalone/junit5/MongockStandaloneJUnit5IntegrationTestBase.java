package io.mongock.test.standalone.junit5;

import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.test.standalone.MongockStandaloneIntegrationTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class MongockStandaloneJUnit5IntegrationTestBase extends MongockStandaloneIntegrationTestBase {
  protected MongockStandaloneJUnit5IntegrationTestBase() {
    super();
  }

  protected MongockStandaloneJUnit5IntegrationTestBase(RunnerBuilder builder) {
    super(builder);
  }

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
