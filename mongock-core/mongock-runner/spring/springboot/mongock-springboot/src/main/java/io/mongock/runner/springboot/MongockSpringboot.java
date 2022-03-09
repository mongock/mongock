package io.mongock.runner.springboot;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.runner.core.executor.ExecutorBuilderDefault;
import io.mongock.runner.core.executor.changelog.ChangeLogService;
import io.mongock.runner.springboot.base.builder.SpringbootBuilderBase;

import static io.mongock.runner.core.builder.BuilderType.COMMUNITY;
import io.mongock.runner.core.executor.ExecutorBuilder;

public final class MongockSpringboot {

  //TODO javadoc
  public static RunnerSpringbootBuilder builder() {
    return new RunnerSpringbootBuilderImpl(new ExecutorBuilderDefault(), new MongockConfiguration());
  }

  public static class RunnerSpringbootBuilderImpl extends SpringbootBuilderBase<RunnerSpringbootBuilderImpl, MongockConfiguration>
      implements RunnerSpringbootBuilder {

    private RunnerSpringbootBuilderImpl(ExecutorBuilder<MongockConfiguration> executorFactory, MongockConfiguration config) {
      super(COMMUNITY, executorFactory, new ChangeLogService(), config);
    }

    @Override
    public RunnerSpringbootBuilderImpl getInstance() {
      return this;
    }
  }
}
