package io.mongock.runner.core.executor;

import io.mongock.api.config.executor.ExecutorConfiguration;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.runner.core.executor.changelog.ChangeLogRuntime;
import io.mongock.runner.core.executor.changelog.ChangeLogServiceBase;
import io.mongock.runner.core.executor.operation.Operation;

public interface ExecutorBuilder<CONFIG extends ExecutorConfiguration> {
  
  ExecutorBuilder<CONFIG> setOperation(Operation op);
  
  ExecutorBuilder<CONFIG> setExecutionId(String executionId);
  
  ExecutorBuilder<CONFIG> setDriver(ConnectionDriver driver);
  
  ExecutorBuilder<CONFIG> setChangeLogService(ChangeLogServiceBase changeLogService);
  
  ExecutorBuilder<CONFIG> setChangeLogRuntime(ChangeLogRuntime changeLogRuntime);
  
  ExecutorBuilder<CONFIG> setConfig(CONFIG config);

  Executor buildExecutor();
}


