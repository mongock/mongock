package io.mongock.driver.api.lock.guard.invoker;

@FunctionalInterface
public interface VoidSupplier {
  void execute();
}
