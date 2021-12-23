package io.mongock.driver.api.driver;

import io.mongock.driver.api.common.Validable;
import io.mongock.driver.api.entry.ChangeEntryService;
import io.mongock.driver.api.lock.LockManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ConnectionDriver extends Validable, DriverLegaciable {
  void initialize();

  LockManager getLockManager();

  ChangeEntryService getChangeEntryService();

  /**
   * This method will be called just before executing a changeSet, for all the changeSets, pulling all the refreshed dependencies
   * @return the dependencies from the driver
   */
  Set<ChangeSetDependency> getDependencies();

  default List<Class<?>> getNonProxyableTypes() {
    return Collections.emptyList();
  }

  default void prepareForExecutionBlock() {
  }

  /**
   * If transaction available, returns the Transactioner
   *
   * @return the Transactioner
   */
  Optional<Transactional> getTransactioner();

  default boolean isTransactionable() {
    return getTransactioner().isPresent();
  }

  void setMigrationRepositoryName(String migrationRepositoryName);

  void setLockRepositoryName(String lockRepositoryName);


  String getMigrationRepositoryName();

  String getLockRepositoryName();
}
