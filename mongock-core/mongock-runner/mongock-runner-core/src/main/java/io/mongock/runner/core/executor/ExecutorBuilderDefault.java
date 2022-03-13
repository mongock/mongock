package io.mongock.runner.core.executor;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.api.exception.MongockException;
import io.mongock.runner.core.executor.operation.Operation;
import io.mongock.runner.core.executor.operation.migrate.MigrateAllExecutor;
import io.mongock.runner.core.executor.operation.migrate.MigrateAllOperation;

public class ExecutorBuilderDefault extends ExecutorBuilderBase<MongockConfiguration> {
  
  @Override
  protected Executor getExecutorByOperation(Operation operation) {
    switch (operation.getId()) {

      case MigrateAllOperation.ID:
        validateScanPackage();
        validateChangeLogService();
        validateChangeLogRuntime();
        return new MigrateAllExecutor(executionId, changeLogService.fetchChangeLogs(), driver, changeLogRuntime, config);

      default:
        throw new MongockException(String.format("Operation '%s' not found. It may be a professional operation and the professional library is not provided ", operation.getId()));
    }
  }
  
  public <CONFIG extends MongockConfiguration, EXECUTOR extends ExecutorBuilderBase<CONFIG>> ExecutorBuilderDefault initializeFrom(EXECUTOR from) {
    this.operation = from.operation;
    this.executionId = from.executionId;
    this.driver = from.driver;
    this.config = from.config;
    this.changeLogService = from.changeLogService;
    this.changeLogRuntime = from.changeLogRuntime;
    return this;
  }
}


