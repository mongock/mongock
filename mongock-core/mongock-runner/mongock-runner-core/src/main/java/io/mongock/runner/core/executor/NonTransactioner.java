package io.mongock.runner.core.executor;

import io.mongock.driver.api.driver.Transactional;

public class NonTransactioner implements Transactional {

  @Override
  public void disableTransaction() {
  }

  @Override
  public void enableTransaction() {
  }

  @Override
  public void executeInTransaction(Runnable operation) {
    operation.run();
  }

}
