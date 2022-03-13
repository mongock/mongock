package io.mongock.runner.core.executor.operation.migrate;

import io.mongock.runner.core.internal.ChangeLogItem;
import io.mongock.api.config.executor.ChangeExecutorConfiguration;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.runner.core.executor.changelog.ChangeLogRuntime;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Set;

@NotThreadSafe
public class MigrateAllExecutor extends MigrateExecutorBase<ChangeExecutorConfiguration> {


  public MigrateAllExecutor(String executionId,
                           Set<ChangeLogItem> changeLogs,
                           ConnectionDriver driver,
                           ChangeLogRuntime changeLogRuntime,
                           ChangeExecutorConfiguration config) {
    super(executionId, changeLogs, driver, changeLogRuntime, config);
  }

}
