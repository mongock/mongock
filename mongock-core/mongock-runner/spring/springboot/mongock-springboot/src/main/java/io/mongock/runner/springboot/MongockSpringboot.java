package io.mongock.runner.springboot;

import io.mongock.runner.core.internal.ChangeLogItem;
import io.mongock.runner.core.internal.ChangeSetItem;
import io.mongock.api.config.MongockConfiguration;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.runner.core.executor.ExecutorFactory;
import io.mongock.runner.core.executor.ExecutorFactoryDefault;
import io.mongock.runner.core.executor.changelog.ChangeLogService;
import io.mongock.runner.springboot.base.builder.SpringbootBuilderBase;

import static io.mongock.runner.core.builder.BuilderType.COMMUNITY;

public final class MongockSpringboot {

  //TODO javadoc
  public static RunnerSpringbootBuilder builder() {
    return new RunnerSpringbootBuilderImpl(new ExecutorFactoryDefault(), new MongockConfiguration());
  }

  public static class RunnerSpringbootBuilderImpl extends SpringbootBuilderBase<RunnerSpringbootBuilderImpl, ChangeLogItem<ChangeSetItem>, ChangeSetItem, ChangeEntry, MongockConfiguration>
      implements RunnerSpringbootBuilder {

    private RunnerSpringbootBuilderImpl(ExecutorFactory<ChangeLogItem<ChangeSetItem>, ChangeSetItem, ChangeEntry, MongockConfiguration> executorFactory, MongockConfiguration config) {
      super(COMMUNITY, executorFactory, new ChangeLogService(), config);
    }

    @Override
    public RunnerSpringbootBuilderImpl getInstance() {
      return this;
    }
  }
}
