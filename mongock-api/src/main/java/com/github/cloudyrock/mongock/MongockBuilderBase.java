package com.github.cloudyrock.mongock;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.migration.api.exception.ChangockException;

import java.util.HashMap;
import java.util.Map;


public abstract class MongockBuilderBase<BUILDER_TYPE extends MongockBuilderBase, MONGOCK_RUNNER extends MongockBase> {

  protected final String changeLogsScanPackage;
  protected ConnectionDriver driver;
  protected long lockAcquiredForMinutes = 24L * 60L;
  protected long maxWaitingForLockMinutes = 3L;
  protected int maxTries = 1;
  protected boolean throwExceptionIfCannotObtainLock = false;
  protected boolean enabled = true;
  protected String changeLogCollectionName = "mongockChangeLog";
  protected String lockCollectionName = "mongockLock";
  protected String startSystemVersion = "0";
  protected String endSystemVersion = String.valueOf(Integer.MAX_VALUE);
  protected Map<String, Object> metadata = new HashMap<>();


  /**
   * <p>Builder constructor takes new API com.mongodb.client.MongoClient, database name and changelog scan package as parameters.
   * </p><p>For more details about MongoClient please see om.mongodb.client.MongoClient docs
   * </p>
   *
   * @param changeLogsScanPackage package path where the changelogs are located
   */
  public MongockBuilderBase(String changeLogsScanPackage) {
    this.changeLogsScanPackage = changeLogsScanPackage;
  }

  /**
   * Feature which enables/disables throwing MongockException if the lock cannot be obtained
   *
   * @param throwExceptionIfCannotObtainLock Mongock will throw MongockException if lock can not be obtained
   * @return Mongock builder for fluent interface
   */
  public BUILDER_TYPE setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
    this.throwExceptionIfCannotObtainLock = throwExceptionIfCannotObtainLock;
    return getInstance();
  }

  /**
   * Sets the Changock  ConnectionDriver(MongoDb Driver 3, MongoDb Sync 4, Spring Data 2, Spring Data 3....
   * @param connectionDriver Changock  ConnectionDriver
   * @return Mongock builder for fluent interface
   */
  public BUILDER_TYPE setDriver(ConnectionDriver connectionDriver) {
    this.driver = connectionDriver;
    return getInstance();
  }


  /**
   * Feature which enables/disables execution
   *
   * @param enabled Migration process will run only if this option is set to true
   * @return Mongock builder for fluent interface
   */
  public BUILDER_TYPE setEnabled(boolean enabled) {
    this.enabled = enabled;
    return getInstance();
  }

  /**
   * Set up the lock with minimal configuration. This implies Mongock will throw an exception if cannot obtains the lock.
   *
   * @param lockAcquiredForMinutes   Acquired time in minutes
   * @param maxWaitingForLockMinutes max time in minutes to wait for the lock in each try.
   * @param maxTries                 number of tries
   * @return Mongock builder for fluent interface
   */
  public BUILDER_TYPE setLockConfig(long lockAcquiredForMinutes, long maxWaitingForLockMinutes, int maxTries) {
    this.lockAcquiredForMinutes = lockAcquiredForMinutes;
    this.maxWaitingForLockMinutes = maxWaitingForLockMinutes;
    this.maxTries = maxTries;
    this.throwExceptionIfCannotObtainLock = true;
    return getInstance();
  }

  /**
   * Set up the lock with default configuration to wait for it and through an exception when cannot obtain it.
   *
   * @return Mongock builder for fluent interface
   */
  public BUILDER_TYPE setLockQuickConfig() {
    setLockConfig(3, 4, 3);
    return getInstance();
  }

  public BUILDER_TYPE setChangeLogCollectionName(String changeLogCollectionName) {
    if(changeLogCollectionName == null || "".equals(changeLogCollectionName)) {
      throw new ChangockException("invalid changeLog collection name");
    }
    this.changeLogCollectionName = changeLogCollectionName;
    return getInstance();
  }

  /**
   * Set up the start Version for versioned schema changes.
   * This shouldn't be confused with the changeSet systemVersion. This is from a consultancy point of view.
   * So the changeSet are tagged with a systemVersion and then when building Mongock, you specify
   * the systemVersion range you want to apply, so all the changeSets tagget with systemVersion inside that
   * range will be applied
   *
   * @param startSystemVersion Version to start with
   * @return Mongock builder for fluent interface
   */
  public BUILDER_TYPE setStartSystemVersion(String startSystemVersion) {
    this.startSystemVersion = startSystemVersion;
    return getInstance();
  }

  /**
   * Set up the end Version for versioned schema changes.
   * This shouldn't be confused with the changeSet systemVersion. This is from a consultancy point of view.
   * So the changeSet are tagged with a systemVersion and then when building Mongock, you specify
   * the systemVersion range you want to apply, so all the changeSets tagget with systemVersion inside that
   * range will be applied
   *
   * @param endSystemVersion Version to end with
   * @return Mongock builder for fluent interface
   */
  public BUILDER_TYPE setEndSystemVersion(String endSystemVersion) {
    this.endSystemVersion = endSystemVersion;
    return getInstance();
  }

  /**
   * Set the metadata for the mongock process. This metadata will be added to each document in the mongockChangeLog
   * collection. This is useful when the system needs to add some extra info to the changeLog.
   *
   * @param metadata Custom metadata object  to be added
   * @return Mongock builder for fluent interface
   */
  public BUILDER_TYPE withMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
    return getInstance();
  }

  protected abstract BUILDER_TYPE getInstance();

}
