package io.mongock.test.standalone;

import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.test.base.MongockIntegrationTestBase;

public class MongockStandaloneIntegrationTestBase extends MongockIntegrationTestBase {
  protected MongockStandaloneIntegrationTestBase() {
    super();
  }

  protected MongockStandaloneIntegrationTestBase(RunnerBuilder builder) {
    super(builder);
  }

}
