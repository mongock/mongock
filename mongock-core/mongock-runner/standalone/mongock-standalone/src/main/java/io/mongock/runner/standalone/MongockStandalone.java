package io.mongock.runner.standalone;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.runner.core.executor.ExecutorBuilder;
import io.mongock.runner.core.executor.ExecutorBuilderDefault;
import io.mongock.runner.core.executor.changelog.ChangeLogService;
import io.mongock.runner.standalone.base.StandaloneBuilderBase;

import static io.mongock.runner.core.builder.BuilderType.COMMUNITY;

public final class MongockStandalone {

  //TODO javadoc
  public static RunnerStandaloneBuilder builder() {
    return new RunnerStandaloneBuilderImpl(new ExecutorBuilderDefault(), new MongockConfiguration());
  }

  public static class RunnerStandaloneBuilderImpl extends StandaloneBuilderBase<RunnerStandaloneBuilderImpl, MongockConfiguration>
      implements RunnerStandaloneBuilder {

    private RunnerStandaloneBuilderImpl(ExecutorBuilder<MongockConfiguration> executorFactory, MongockConfiguration config) {
      super(COMMUNITY, executorFactory, new ChangeLogService(), config);
    }

    @Override
    public RunnerStandaloneBuilderImpl getInstance() {
      return this;
    }
  }


}
