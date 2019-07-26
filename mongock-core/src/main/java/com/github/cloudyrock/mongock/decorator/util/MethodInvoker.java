package com.github.cloudyrock.mongock.decorator.util;

import com.github.cloudyrock.mongock.LockChecker;

import java.util.function.Supplier;

public class MethodInvoker {

  private final LockChecker lockChecker;

  public MethodInvoker(LockChecker lockChecker) {
    this.lockChecker = lockChecker;
  }

  public <T> T invoke(Supplier<T> supplier) {
    lockChecker.ensureLockDefault();
    return supplier.get();
  }

  public void invoke(VoidSupplier supplier) {
    lockChecker.ensureLockDefault();
    supplier.execute();
  }

}
