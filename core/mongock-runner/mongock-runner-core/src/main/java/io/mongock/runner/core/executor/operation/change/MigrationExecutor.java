package io.mongock.runner.core.executor.operation.change;

import io.mongock.api.ChangeLogItem;
import io.mongock.api.ChangeSetItem;
import io.mongock.api.config.executor.ChangeExecutorConfiguration;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.runner.core.executor.changelog.ChangeLogRuntime;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.SortedSet;

@NotThreadSafe
public class MigrationExecutor extends MigrationExecutorBase<ChangeExecutorConfiguration> {


  public MigrationExecutor(String executionId,
                           SortedSet<ChangeLogItem<ChangeSetItem>> changeLogs,
                           ConnectionDriver<ChangeEntry> driver,
                           ChangeLogRuntime changeLogRuntime,
                           ChangeExecutorConfiguration config) {
    super(executionId, changeLogs, driver, changeLogRuntime, config);
  }

}
