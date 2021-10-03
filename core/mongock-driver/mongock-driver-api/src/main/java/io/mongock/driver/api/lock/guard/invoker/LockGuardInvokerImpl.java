package io.mongock.driver.api.lock.guard.invoker;

import io.mongock.driver.api.lock.LockManager;

import java.util.function.Supplier;

public class LockGuardInvokerImpl implements LockGuardInvoker {

  private final LockManager lockManager;

  public LockGuardInvokerImpl(LockManager lockManager) {
    this.lockManager = lockManager;
  }

  @Override
  public <T> T invoke(Supplier<T> supplier) {
    lockManager.ensureLockDefault();
    return supplier.get();
  }

  @Override
  public void invoke(VoidSupplier supplier) {
    lockManager.ensureLockDefault();
    supplier.execute();
  }

}
