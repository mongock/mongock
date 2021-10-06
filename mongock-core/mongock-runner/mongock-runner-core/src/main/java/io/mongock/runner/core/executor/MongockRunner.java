package io.mongock.runner.core.executor;

import io.mongock.api.exception.MongockException;

public interface MongockRunner {

  boolean isExecutionInProgress();

  boolean isEnabled();

  void forceEnable();

  void execute() throws MongockException;
}
