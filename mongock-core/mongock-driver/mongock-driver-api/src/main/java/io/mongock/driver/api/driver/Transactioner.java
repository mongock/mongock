package io.mongock.driver.api.driver;

public interface Transactioner {

  void disableTransaction();

  void enableTransaction();

  void executeInTransaction(Runnable operation);
}
