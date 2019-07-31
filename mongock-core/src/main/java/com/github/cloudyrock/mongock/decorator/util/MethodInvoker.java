package com.github.cloudyrock.mongock.decorator.util;

import java.util.function.Supplier;

public interface MethodInvoker {
  <T> T invoke(Supplier<T> supplier);

  void invoke(VoidSupplier supplier);
}
