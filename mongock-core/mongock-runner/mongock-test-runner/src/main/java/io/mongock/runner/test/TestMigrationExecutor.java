package io.mongock.runner.test;

import io.mongock.runner.core.internal.ChangeLogItem;
import io.mongock.api.config.MongockConfiguration;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.runner.core.executor.changelog.ChangeLogRuntime;
import io.mongock.runner.core.executor.operation.change.MigrationExecutor;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.SortedSet;

@NotThreadSafe
public class TestMigrationExecutor extends MigrationExecutor {


  public TestMigrationExecutor(String executionId,
                               SortedSet<ChangeLogItem> changeLogs,
                               ConnectionDriver driver,
                               ChangeLogRuntime changeLogRuntime,
                               MongockConfiguration config) {
    //todo remove null
    super(executionId, changeLogs, driver, changeLogRuntime, config);
  }



}
