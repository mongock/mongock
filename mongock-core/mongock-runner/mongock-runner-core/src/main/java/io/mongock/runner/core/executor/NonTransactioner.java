package io.mongock.runner.core.executor;

import io.mongock.driver.api.driver.Transactionable;

public class NonTransactioner implements Transactionable {

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
