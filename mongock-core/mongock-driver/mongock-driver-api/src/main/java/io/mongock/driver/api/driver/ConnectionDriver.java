package io.mongock.driver.api.driver;

import io.mongock.api.exception.MongockException;
import io.mongock.driver.api.common.Validable;
import io.mongock.driver.api.entry.ChangeEntryService;
import io.mongock.driver.api.lock.LockManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ConnectionDriver extends Validable {

  void initialize();

  LockManager getLockManager();

  ChangeEntryService getChangeEntryService();

  /**
   * This method will be called just before executing a changeSet, for all the changeSets, pulling all the refreshed dependencies
   * @return the dependencies from the driver
   */
  Set<ChangeSetDependency> getDependencies();

  /**
   * If transaction available, returns the Transactioner
   *
   * @return the Transactioner
   */
  Optional<Transactional> getTransactioner();

  void setMigrationRepositoryName(String migrationRepositoryName);

  void setLockRepositoryName(String lockRepositoryName);

  /****************
   * Default implementations
   ****************/
  default boolean isTransactionable() {
    return getTransactioner().isPresent();
  }

  default void runValidation() throws MongockException {
  }

  default List<Class<?>> getNonProxyableTypes() {
    return Collections.emptyList();
  }

  default void prepareForExecutionBlock() {
  }
}
