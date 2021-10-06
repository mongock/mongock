package io.mongock.driver.api.lock.guard.proxy.util;

import io.changock.migration.api.annotations.NonLockGuarded;

@NonLockGuarded
public class InterfaceTypeImplNonLockGuarded implements InterfaceType {
  @Override
  public void fakeFinalize() {

  }
}
