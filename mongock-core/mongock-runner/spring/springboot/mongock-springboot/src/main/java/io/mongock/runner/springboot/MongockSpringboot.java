package io.mongock.runner.springboot;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.runner.core.executor.ExecutorBuilderDefault;
import io.mongock.runner.core.executor.changelog.ChangeLogService;
import io.mongock.runner.springboot.base.builder.SpringbootBuilderBase;

import static io.mongock.runner.core.builder.BuilderType.COMMUNITY;
import io.mongock.runner.core.executor.ExecutorBuilder;
import io.mongock.runner.core.executor.changelog.ChangeLogServiceBase;

public final class MongockSpringboot {

  //TODO javadoc
  public static RunnerSpringbootBuilder builder() {
    return new RunnerSpringbootBuilderImpl(new ExecutorBuilderDefault(), new ChangeLogService(), new MongockConfiguration());
  }

  public static class RunnerSpringbootBuilderImpl extends SpringbootBuilderBase<RunnerSpringbootBuilderImpl, MongockConfiguration>
      implements RunnerSpringbootBuilder {

    RunnerSpringbootBuilderImpl(ExecutorBuilder<MongockConfiguration> executorFactory, ChangeLogServiceBase changeLogService, MongockConfiguration config) {
      super(COMMUNITY, executorFactory, changeLogService, config);
    }

    @Override
    public RunnerSpringbootBuilderImpl getInstance() {
      return this;
    }
  }
}
