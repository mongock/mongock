package io.mongock.runner.core.util;


import io.changock.migration.api.annotations.NonLockGuarded;

public interface InterfaceDependency {

  default String getValue() {
    return "value";
  }

  default InterfaceDependency getInstance() {
    return new InterfaceDependencyImpl();
  }

  @NonLockGuarded
  default String getNonLockguardedValue() {
    return "value";
  }

}
