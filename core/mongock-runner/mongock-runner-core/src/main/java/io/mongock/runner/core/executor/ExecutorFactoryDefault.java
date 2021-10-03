package io.mongock.runner.core.executor;

import io.mongock.api.ChangeLogItem;
import io.mongock.api.ChangeSetItem;
import io.mongock.api.config.MongockConfiguration;
import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.runner.core.executor.changelog.ChangeLogRuntime;
import io.mongock.runner.core.executor.operation.Operation;
import io.mongock.runner.core.executor.operation.change.MigrationExecutor;
import io.mongock.runner.core.executor.operation.change.MigrationOp;
import io.mongock.runner.core.executor.operation.list.ListChangesExecutor;
import io.mongock.runner.core.executor.operation.list.ListChangesOp;

import java.util.SortedSet;

public class ExecutorFactoryDefault implements ExecutorFactory<ChangeLogItem<ChangeSetItem>, ChangeSetItem, ChangeEntry, MongockConfiguration> {

  @Override
  public  Executor getExecutor(Operation op,
								 String executionId,
								 SortedSet<ChangeLogItem<ChangeSetItem>> changeLogs,
								 ConnectionDriver<ChangeEntry> driver,
								 ChangeLogRuntime changeLogRuntime,
								 MongockConfiguration config) {
    switch (op.getId()) {

      case MigrationOp.ID:
        return new MigrationExecutor(executionId, changeLogs, driver, changeLogRuntime, config);

      case ListChangesOp.ID:
        return new ListChangesExecutor();

      default:
        throw new MongockException(String.format("Operation '%s' not found. It may be a professional operation and the professional library is not provided ", op.getId()));
    }
  }


}


