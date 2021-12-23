package io.mongock.driver.api.driver;

public interface Transactionable {

  void disableTransaction();

  void enableTransaction();

  void executeInTransaction(Runnable operation);
}
