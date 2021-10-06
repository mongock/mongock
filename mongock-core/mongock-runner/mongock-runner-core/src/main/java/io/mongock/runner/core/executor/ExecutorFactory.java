package io.mongock.runner.core.executor;

import io.mongock.api.ChangeLogItem;
import io.mongock.api.ChangeSetItem;
import io.mongock.api.config.executor.ExecutorConfiguration;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.api.entry.ChangeEntry;
import io.mongock.runner.core.executor.changelog.ChangeLogRuntime;
import io.mongock.runner.core.executor.operation.Operation;

import java.util.SortedSet;

public interface ExecutorFactory<
    CHANGELOG extends ChangeLogItem<CHANGESET>,
    CHANGESET extends ChangeSetItem,
    CHANGE_ENTRY extends ChangeEntry,
    CONFIG extends ExecutorConfiguration> {

  Executor getExecutor(Operation op,
						  String executionId,
						  SortedSet<CHANGELOG> changeLogs,
						  ConnectionDriver<CHANGE_ENTRY> driver,
						  ChangeLogRuntime changeLogRuntime,
						  CONFIG config);


}


