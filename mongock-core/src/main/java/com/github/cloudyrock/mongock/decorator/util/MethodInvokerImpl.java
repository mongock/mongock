package com.github.cloudyrock.mongock.decorator.util;

import com.github.cloudyrock.mongock.LockChecker;

import java.util.function.Supplier;

public class MethodInvokerImpl implements MethodInvoker {

  private final LockChecker lockChecker;

  public MethodInvokerImpl(LockChecker lockChecker) {
    this.lockChecker = lockChecker;
  }

  @Override
  public <T> T invoke(Supplier<T> supplier) {
    lockChecker.ensureLockDefault();
    return supplier.get();
  }

  @Override
  public void invoke(VoidSupplier supplier) {
    lockChecker.ensureLockDefault();
    supplier.execute();
  }

}
