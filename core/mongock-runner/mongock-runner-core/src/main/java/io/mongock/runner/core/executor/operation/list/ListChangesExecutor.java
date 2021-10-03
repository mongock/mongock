package io.mongock.runner.core.executor.operation.list;

import io.mongock.runner.core.executor.Executor;

public class ListChangesExecutor implements Executor {

  @Override
  public ListChangesResult executeMigration() {
    return new ListChangesResult();
  }

  @Override
  public boolean isExecutionInProgress() {
    return false;
  }
}
