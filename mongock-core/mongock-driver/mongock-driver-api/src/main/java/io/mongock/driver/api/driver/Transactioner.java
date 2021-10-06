package io.mongock.driver.api.driver;

@FunctionalInterface
public interface Transactioner {
  void executeInTransaction(Runnable operation);
}
