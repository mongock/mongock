package io.mongock.driver.api.driver;

public interface Transactional {

  void disableTransaction();

  void enableTransaction();

  void executeInTransaction(Runnable operation);
}
