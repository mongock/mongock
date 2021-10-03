package io.mongock.driver.core.lock;

/**
 * @since 04/04/2018
 */
public class LockPersistenceException extends RuntimeException {

  /**
   * Condition to update/insert lock
   */
  private final String acquireLockQuery;

  /**
   * NewLock entity
   */
  private final String newLockEntity;

  /**
   * Further db error detail
   */
  private final String dbErrorDetail;

  public LockPersistenceException(String acquireLockQuery, String newLockEntity, String dbErrorDetail) {
    this.acquireLockQuery = acquireLockQuery;
    this.newLockEntity = newLockEntity;
    this.dbErrorDetail = dbErrorDetail;
  }

  public String getAcquireLockQuery() {
    return acquireLockQuery;
  }

  public String getNewLockEntity() {
    return newLockEntity;
  }

  public String getDbErrorDetail() {
    return dbErrorDetail;
  }

  @Override
  public String getMessage() {
    return toString();
  }

  @Override
  public String toString() {
    return "LockPersistenceException{" +
        ", acquireLockQuery='" + acquireLockQuery + '\'' +
        ", newLockEntity='" + newLockEntity + '\'' +
        ", dbErrorDetail='" + dbErrorDetail + '\'' +
        "} ";
  }
}
