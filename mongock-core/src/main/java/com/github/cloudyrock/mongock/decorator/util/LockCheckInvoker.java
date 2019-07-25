package com.github.cloudyrock.mongock.decorator.util;

import com.github.cloudyrock.mongock.LockChecker;

import java.util.function.Supplier;

public class LockCheckInvoker {

  private final LockChecker lockChecker;

  public LockCheckInvoker(LockChecker lockChecker) {
    this.lockChecker = lockChecker;
  }

  public <T> T invoke(Supplier<T> supplier) {
    return supplier.get();
  }

  public void invoke(VoidSupplier supplier) {
    supplier.execute();
  }


}
