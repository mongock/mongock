package io.mongock.runner.standalone;

import io.mongock.api.ChangeLogItem;
import io.mongock.api.ChangeSetItem;
import io.mongock.api.config.MongockConfiguration;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.runner.core.executor.ExecutorFactory;
import io.mongock.runner.core.executor.ExecutorFactoryDefault;
import io.mongock.runner.core.executor.changelog.ChangeLogService;
import io.mongock.runner.standalone.base.StandaloneBuilderBase;

import static io.mongock.runner.core.builder.BuilderType.COMMUNITY;

public final class MongockStandalone {

  //TODO javadoc
  public static RunnerStandaloneBuilder builder() {
    return new RunnerStandaloneBuilderImpl(new ExecutorFactoryDefault(), new MongockConfiguration());
  }

  public static class RunnerStandaloneBuilderImpl extends StandaloneBuilderBase<RunnerStandaloneBuilderImpl, ChangeLogItem<ChangeSetItem>, ChangeSetItem, ChangeEntry, MongockConfiguration>
      implements RunnerStandaloneBuilder {

    private RunnerStandaloneBuilderImpl(ExecutorFactory<ChangeLogItem<ChangeSetItem>, ChangeSetItem, ChangeEntry, MongockConfiguration> executorFactory, MongockConfiguration config) {
      super(COMMUNITY, executorFactory, new ChangeLogService(), config);
    }

    @Override
    public RunnerStandaloneBuilderImpl getInstance() {
      return this;
    }
  }


}
