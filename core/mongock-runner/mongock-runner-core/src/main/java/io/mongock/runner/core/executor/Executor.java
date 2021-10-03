package io.mongock.runner.core.executor;

public interface Executor {

  Object executeMigration();


  boolean isExecutionInProgress();
}
