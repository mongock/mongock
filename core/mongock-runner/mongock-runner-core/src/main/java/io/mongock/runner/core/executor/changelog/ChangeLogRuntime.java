package io.mongock.runner.core.executor.changelog;

import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.driver.ChangeSetDependency;
import io.mongock.driver.api.lock.LockManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;


public interface ChangeLogRuntime {
  void initialize(LockManager lockManager);

  void updateDriverDependencies(Set<ChangeSetDependency> dependencies);

  void runChangeSet(Object changeLogInstance, Method changeSetMethod) throws IllegalAccessException, InvocationTargetException;

  Object getInstance(Class<?> type) throws MongockException;
}
