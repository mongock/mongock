package io.mongock.runner.core.executor;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.api.exception.MongockException;
import io.mongock.runner.core.executor.operation.Operation;
import io.mongock.runner.core.executor.operation.migrate.MigrateAllExecutor;
import io.mongock.runner.core.executor.operation.migrate.MigrateAllOperation;
import io.mongock.runner.core.executor.system.SystemUpdateExecutor;
import java.util.Collections;
import java.util.List;

public class ExecutorBuilderDefault extends ExecutorBuilderBase<MongockConfiguration> {
  
  private static final List<String> SYSTEM_CHANGES_BASE_PACKAGES = Collections.singletonList("io.mongock.runner.core.executor.system.changes");
  
  @Override
  protected Executor getSystemExecutor() {
    validateChangeLogService();
    validateChangeLogRuntime();
    return new SystemUpdateExecutor(executionId, driver, changeLogService, changeLogRuntime, config, SYSTEM_CHANGES_BASE_PACKAGES);
  }
  
  @Override
  protected Executor getExecutorByOperation(Operation operation) {
    switch (operation.getId()) {

      case MigrateAllOperation.ID:
        validateScanPackage();
        validateChangeLogService();
        validateChangeLogRuntime();
        return new MigrateAllExecutor(executionId, changeLogService, driver, changeLogRuntime, annotationFilter, config);

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
    this.annotationFilter = from.annotationFilter;
    return this;
  }
}


