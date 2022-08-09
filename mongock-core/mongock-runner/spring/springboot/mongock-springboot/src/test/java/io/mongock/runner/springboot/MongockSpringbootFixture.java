package io.mongock.runner.springboot;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.runner.core.executor.changelog.ChangeLogService;
import io.mongock.runner.springboot.MongockSpringboot.RunnerSpringbootBuilderImpl;
import io.mongock.runner.springboot.util.ExecutorBuilderFixture;

public class MongockSpringbootFixture {
  
  public static RunnerSpringbootBuilder builder(boolean withSystemUpdate) {
    return new RunnerSpringbootBuilderImpl(new ExecutorBuilderFixture(withSystemUpdate), new ChangeLogService(), new MongockConfiguration());
  }
}
