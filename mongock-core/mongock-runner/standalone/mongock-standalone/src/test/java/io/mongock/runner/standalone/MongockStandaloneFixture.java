package io.mongock.runner.standalone;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.runner.core.executor.changelog.ChangeLogService;
import io.mongock.runner.standalone.MongockStandalone.RunnerStandaloneBuilderImpl;
import io.mongock.runner.standalone.util.ExecutorBuilderFixture;

public class MongockStandaloneFixture {
  
  public static RunnerStandaloneBuilder builder(boolean withSystemUpdate) {
    return new RunnerStandaloneBuilderImpl(new ExecutorBuilderFixture(withSystemUpdate), new ChangeLogService(), new MongockConfiguration());
  }
}
