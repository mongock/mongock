package io.mongock.runner.test;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.builder.RunnerBuilderBase;
import io.mongock.runner.core.executor.changelog.ChangeLogService;
import io.mongock.runner.core.executor.dependency.DependencyManager;

import static io.mongock.runner.core.builder.BuilderType.COMMUNITY;
import io.mongock.runner.core.executor.ExecutorBuilder;
import io.mongock.runner.core.executor.ExecutorBuilderDefault;

public class TestMongock {

  public static Builder builder() {
    return new Builder(new ExecutorBuilderDefault());
  }

  public static class Builder extends RunnerBuilderBase<Builder, MongockConfiguration> implements RunnerBuilder<Builder, MongockConfiguration> {

    private Builder(ExecutorBuilder<MongockConfiguration> executorBuilder) {
      super(COMMUNITY, executorBuilder, new ChangeLogService(), new DependencyManager(), new MongockConfiguration());
    }

    @Override
    public Builder getInstance() {
      return this;
    }

  }
}
