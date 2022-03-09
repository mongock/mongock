package io.mongock.runner.core.executor;

import io.mongock.api.config.MongockConfiguration;
import io.mongock.api.exception.MongockException;
import io.mongock.runner.core.executor.operation.Operation;
import io.mongock.runner.core.executor.operation.change.MigrationExecutor;
import io.mongock.runner.core.executor.operation.change.MigrationOp;
import io.mongock.runner.core.internal.ChangeLogItem;
import io.mongock.runner.core.internal.ChangeSetItem;

public class ExecutorBuilderDefault extends ExecutorBuilderBase<ChangeLogItem<ChangeSetItem>, ChangeSetItem, MongockConfiguration> {
  
  @Override
  protected Executor getExecutorByOperation(Operation operation) {
    switch (operation.getId()) {

      case MigrationOp.ID:
        validateScanPackage();
        validateChangeLogService();
        validateChangeLogRuntime();
        return new MigrationExecutor(executionId, changeLogService.fetchChangeLogs(), driver, changeLogRuntime, config);

      default:
        throw new MongockException(String.format("Operation '%s' not found. It may be a professional operation and the professional library is not provided ", operation.getId()));
    }
  }
}


