package io.mongock.driver.api.lock.guard.decorator;

import io.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;

public interface Invokable {
  LockGuardInvoker getInvoker();
}
