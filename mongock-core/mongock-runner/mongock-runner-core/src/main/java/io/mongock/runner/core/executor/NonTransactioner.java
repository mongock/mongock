package io.mongock.runner.core.executor;

import io.mongock.driver.api.driver.Transactioner;

public class NonTransactioner implements Transactioner {

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
