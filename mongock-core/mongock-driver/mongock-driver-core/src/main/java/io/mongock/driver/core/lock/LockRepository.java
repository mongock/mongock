package io.mongock.driver.core.lock;

import io.mongock.driver.api.common.RepositoryIndexable;
import io.mongock.utils.Process;

/**
 * <p>Repository interface to manage lock in database, which will be used by LockManager</p>
 */
public interface LockRepository extends ILockRepository<LockEntry>, RepositoryIndexable, Process {
}
