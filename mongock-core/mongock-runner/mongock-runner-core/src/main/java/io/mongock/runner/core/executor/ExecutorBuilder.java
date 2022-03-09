package io.mongock.runner.core.executor;

import io.mongock.api.config.executor.ExecutorConfiguration;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.runner.core.executor.changelog.ChangeLogRuntime;
import io.mongock.runner.core.executor.changelog.ChangeLogServiceBase;
import io.mongock.runner.core.executor.operation.Operation;
import io.mongock.runner.core.internal.ChangeLogItem;
import io.mongock.runner.core.internal.ChangeSetItem;

public interface ExecutorBuilder<CHANGELOG extends ChangeLogItem<CHANGESET>,
                                 CHANGESET extends ChangeSetItem,
                                 CONFIG extends ExecutorConfiguration> {
  
  ExecutorBuilder<CHANGELOG, CHANGESET, CONFIG> setOperation(Operation op);
  
  ExecutorBuilder<CHANGELOG, CHANGESET, CONFIG> setExecutionId(String executionId);
  
  ExecutorBuilder<CHANGELOG, CHANGESET, CONFIG> setDriver(ConnectionDriver driver);
  
  ExecutorBuilder<CHANGELOG, CHANGESET, CONFIG> setChangeLogService(ChangeLogServiceBase<CHANGELOG, CHANGESET> changeLogService);
  
  ExecutorBuilder<CHANGELOG, CHANGESET, CONFIG> setChangeLogRuntime(ChangeLogRuntime changeLogRuntime);
  
  ExecutorBuilder<CHANGELOG, CHANGESET, CONFIG> setConfig(CONFIG config);

  Executor buildExecutor();
}


