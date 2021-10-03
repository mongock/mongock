package io.mongock.utils;

import java.util.function.Supplier;

public class OrSupplier<T> {
  public T or(Supplier<T> supplier) {
    return supplier.get();
  }

}
