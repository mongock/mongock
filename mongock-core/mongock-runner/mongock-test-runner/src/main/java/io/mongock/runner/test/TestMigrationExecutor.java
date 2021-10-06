package io.mongock.runner.test;

import io.mongock.api.ChangeLogItem;
import io.mongock.api.ChangeSetItem;
import io.mongock.api.config.MongockConfiguration;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.runner.core.executor.changelog.ChangeLogRuntime;
import io.mongock.runner.core.executor.operation.change.MigrationExecutor;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.SortedSet;

@NotThreadSafe
public class TestMigrationExecutor extends MigrationExecutor {


  public TestMigrationExecutor(String executionId,
                               SortedSet<ChangeLogItem<ChangeSetItem>> changeLogs,
                               ConnectionDriver<ChangeEntry> driver,
                               ChangeLogRuntime changeLogRuntime,
                               MongockConfiguration config) {
    //todo remove null
    super(executionId, changeLogs, driver, changeLogRuntime, config);
  }



}
